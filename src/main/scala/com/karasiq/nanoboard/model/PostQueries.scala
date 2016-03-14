package com.karasiq.nanoboard.model

import java.time.{Instant, LocalDate}

import com.karasiq.nanoboard.NanoboardMessage
import com.karasiq.nanoboard.api.NanoboardMessageData
import slick.driver.H2Driver.api._
import slick.lifted.{Compiled, ConstColumn}

import scala.concurrent.ExecutionContext

trait PostQueries { self: Tables with ConfigQueries with ContainerQueries ⇒
  object Post {
    def addReply(m: NanoboardMessage)(implicit ec: ExecutionContext) = {
      for {
        container ← Container.forUrl(s"local://${LocalDate.now()}")
        _ ← DBIO.seq(insertMessage(container, m), pendingPosts.forceInsertQuery {
          val exists = (for (p <- pendingPosts if p.hash === m.hash) yield ()).exists
          for (message <- Query(m.hash) if !exists) yield message
        }, deletedPosts.filter(_.hash === m.hash).delete)
      } yield NanoboardMessageData(Some(container), Some(m.parent), m.hash, m.text, 0)
    }

    def insertMessage(container: Long, m: NanoboardMessage) = posts.forceInsertQuery {
      val deleted = (for (p <- deletedPosts if p.hash === m.hash) yield ()).exists
      val exists = (for (p <- posts if p.hash === m.hash) yield ()).exists
      val insert = (m.hash, m.parent, m.text, Instant.now().toEpochMilli, container) <> (DBPost.tupled, DBPost.unapply)
      for (message <- Query(insert) if !deleted && !exists) yield message
    }

    def insertMessages(container: Long, messages: Seq[NanoboardMessage]) = {
      DBIO.sequence(messages.map(insertMessage(container, _)))
    }

    private def recentQuery(offset: ConstColumn[Long], count: ConstColumn[Long]) = {
      posts
        .sortBy(_.firstSeen.desc)
        .drop(offset)
        .take(count)
        .map(post ⇒ (post, posts.filter(_.parent === post.hash).length))
    }

    private val recentCompiled = Compiled(recentQuery _)

    def recent(offset: Long, count: Long)(implicit ec: ExecutionContext) = {
      recentCompiled(offset, count)
        .result
        .map(_.map {
          case (post, answers) ⇒
            post.asThread(answers)
        })
    }

    private def pendingQuery(offset: ConstColumn[Long], count: ConstColumn[Long]) = {
      pendingPosts
        .flatMap(_.post)
        .sortBy(_.firstSeen.asc)
        .drop(offset)
        .take(count)
    }

    private val pendingCompiled = Compiled(pendingQuery _)

    def pending(offset: Long, count: Long)(implicit ec: ExecutionContext) = {
      pendingCompiled(offset, count)
        .result
        .map(_.map(_.asThread(0)))
    }

    private def getQuery(hash: Rep[String]) = {
      posts
        .filter(_.hash === hash)
        .map(post ⇒ (post, posts.filter(_.parent === post.hash).length))
    }

    private val getCompiled = Compiled(getQuery _)

    def get(hash: String)(implicit ec: ExecutionContext) = {
      getCompiled(hash)
        .result
        .headOption
        .map(_.map {
          case (post, answers) ⇒
            post.asThread(answers)
        })
    }

    private def threadQuery(hash: Rep[String], offset: ConstColumn[Long], count: ConstColumn[Long]) = {
      val query = posts.filter(_.parent === hash)
        .sortBy(_.firstSeen.desc)
        .drop(offset)
        .take(count)

      def withAnswerCount(query: Query[Post, DBPost, Seq]) = query.map { post ⇒
        (post, posts.filter(_.parent === post.hash).length)
      }

      withAnswerCount(query)
    }

    private val threadCompiled = Compiled(threadQuery _)

    def thread(hash: String, offset: Long, count: Long)(implicit ec: ExecutionContext) = {
      for {
        opPost ← get(hash)
        answers ← threadCompiled(hash, offset, count).result
      } yield opPost.toVector ++ answers.map {
        case (post, answersCount) ⇒
          post.asThread(answersCount)
      }
    }

    def delete(hash: String)(implicit ec: ExecutionContext) = {
      for {
        _ ← DBIO.seq(
          pendingPosts.filter(_.hash === hash).delete,
          Category.delete(hash),
          deletedPosts.insertOrUpdate(hash),
          posts.filter(_.hash === hash).delete
        )
      } yield hash
    }

    def deleteCascade(hash: String)(implicit ec: ExecutionContext) = {
      def deleteCascadeRec(hash: String): DBIOAction[Seq[String], NoStream, Effect.Write with Effect.Read] = {
        val query = posts.filter(_.parent === hash)
        for {
          deleted ← query.map(_.hash).result
          descendants ← DBIO.sequence(deleted.map(deleteCascadeRec))
          _ ← delete(hash)
        } yield Seq(hash) ++ deleted ++ descendants.flatten
      }

      deleteCascadeRec(hash)
    }
  }
}
