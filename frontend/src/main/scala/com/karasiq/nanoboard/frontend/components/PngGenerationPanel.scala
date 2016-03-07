package com.karasiq.nanoboard.frontend.components

import com.karasiq.bootstrap.BootstrapImplicits._
import com.karasiq.bootstrap.form.{Form, FormInput}
import com.karasiq.bootstrap.{Bootstrap, BootstrapHtmlComponent}
import com.karasiq.nanoboard.frontend.NanoboardContext
import com.karasiq.nanoboard.frontend.api.{NanoboardApi, NanoboardMessageData}
import com.karasiq.nanoboard.frontend.components.post.NanoboardPost
import com.karasiq.nanoboard.frontend.utils.Notifications.Layout
import com.karasiq.nanoboard.frontend.utils.{Blobs, Notifications}
import org.scalajs.dom
import org.scalajs.dom.html.Input
import rx._

import scala.concurrent.ExecutionContext
import scala.scalajs.js
import scala.util.{Failure, Success}
import scalatags.JsDom.all._

object PngGenerationPanel {
  def apply()(implicit ec: ExecutionContext, ctx: Ctx.Owner, controller: NanoboardController): PngGenerationPanel = {
    new PngGenerationPanel
  }
}

final class PngGenerationPanel(implicit ec: ExecutionContext, ctx: Ctx.Owner, controller: NanoboardController) extends BootstrapHtmlComponent[dom.html.Div] with PostsContainer {
  override val posts = Var(Vector.empty[NanoboardMessageData])

  override val context: Var[NanoboardContext] = Var(NanoboardContext.Categories)

  private val loading = Var(false)

  override def addPost(post: NanoboardMessageData): Unit = {
    posts() = posts.now :+ post
  }

  override def deletePost(post: NanoboardMessageData): Unit = {
    posts() = posts.now.filterNot(p ⇒ p.hash == post.hash || p.parent.contains(post.hash))
  }

  override def update(): Unit = {
    loading() = true
    NanoboardApi.pending().onComplete {
      case Success(posts) ⇒
        this.posts() = posts
        loading() = false

      case Failure(_) ⇒
        loading() = false
    }
  }

  private val pendingContainer = Rx[Frag] {
    val posts = this.posts()
    if (posts.nonEmpty) Bootstrap.well(
      marginTop := 20.px,
      h3("Pending posts"),
      for (p ← posts) yield NanoboardPost(showParent = true, showAnswers = false, p)
    ) else ()
  }

  private val form = Form(
    FormInput.number("Pending posts", name := "pending", value := 10, min := 0),
    FormInput.number("Random posts", name := "random", value := 30, min := 0),
    FormInput.text("Output format", name := "format", value := "png"),
    FormInput.file("Data container", name := "container"),
    Form.submit("Generate container image")("disabled".classIf(loading)),
    onsubmit := Bootstrap.jsSubmit { frm ⇒
      if (!loading.now) {
        loading() = true
        def input(name: String) = frm(name).asInstanceOf[Input]
        input("container").files.headOption match {
          case Some(file) ⇒
            val pending = input("pending").valueAsNumber
            val random = input("random").valueAsNumber
            val format: String = input("format").value

            NanoboardApi.generateContainer(pending, random, format, file).onComplete {
              case Success(blob) ⇒
                loading() = false
                Blobs.saveBlob(blob, s"${js.Date.now()}.$format")
                update()

              case Failure(exc) ⇒
                loading() = false
                Notifications.error(exc)("Container generation failure", Layout.topRight, 1500)
            }

          case None ⇒
            loading() = false
            Notifications.warning("Container file not selected", Layout.topRight)
        }
      }
    }
  )

  override def renderTag(md: Modifier*) = {
    div(form, pendingContainer)
  }

  update()
}