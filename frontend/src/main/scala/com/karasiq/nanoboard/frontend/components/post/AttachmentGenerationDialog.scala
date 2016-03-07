package com.karasiq.nanoboard.frontend.components.post

import com.karasiq.bootstrap.Bootstrap
import com.karasiq.bootstrap.BootstrapImplicits._
import com.karasiq.bootstrap.form.{Form, FormInput}
import com.karasiq.bootstrap.modal.Modal
import com.karasiq.nanoboard.frontend.api.NanoboardApi
import com.karasiq.nanoboard.frontend.utils.CancelledException
import org.scalajs.dom.html.Input
import org.scalajs.dom.raw.File
import rx._

import scala.concurrent.{ExecutionContext, Future, Promise}
import scalatags.JsDom.all._

private[components] object AttachmentGenerationDialog {
  def apply()(implicit ctx: Ctx.Owner, ec: ExecutionContext): AttachmentGenerationDialog = {
    new AttachmentGenerationDialog
  }
}

private[components] final class AttachmentGenerationDialog(implicit ctx: Ctx.Owner, ec: ExecutionContext) {
  val format = Var("jpeg")
  val size = Var(500)
  val quality = Var(70)
  val file = Var[Option[File]](None)

  val ready = Rx {
    format().nonEmpty && size() > 0 && file().nonEmpty && (1 to 100).contains(quality())
  }

  def generate(): Future[String] = {
    val promise = Promise[String]
    val modal = Modal("Attachment generation")
      .withBody(Form(
        FormInput.text("Format", name := "format", format.reactiveInput, placeholder := "Java ImageIO supported format name"),
        FormInput.number("Size", name := "size", min := 10, size.reactiveInput, placeholder := "Max height/width in pixels"),
        FormInput.number("Quality", name := "quality", min := 1, max := 100, quality.reactiveInput, placeholder := "Image quality"),
        FormInput.file("File", name := "image", file.reactiveRead("change", e ⇒ e.asInstanceOf[Input].files.headOption)),
        onsubmit := Bootstrap.jsSubmit(_ ⇒ ())
      ))
      .withButtons(
        Modal.closeButton("Cancel")(onclick := Bootstrap.jsClick { _ ⇒
          promise.failure(CancelledException)
        }),
        Modal.button("Generate", Modal.dismiss, ready.reactiveShow, onclick := Bootstrap.jsClick { _ ⇒
          promise.completeWith(NanoboardApi.generateAttachment(format.now, size.now, quality.now, file.now.get))
        })
      )
    modal.show(backdrop = false)
    promise.future
  }
}