package com.karasiq.nanoboard.frontend.locales

object English extends BoardLocale {
  def nanoboard = "Nanoboard"
  def generateContainer = "Generate container"
  def cancel = "Cancel"
  def dataContainer = "Source file"
  def dequeue = "Dequeue"
  def insertImage = "Insert image"
  def recentPostsFrom(post: Int) = s"Recent posts (from $post)"
  def categories = "Categories"
  def pendingPosts = "Pending posts"
  def containerGeneration = "Container generation"
  def delete = "Delete"
  def imageFormat = "Image format"
  def enqueue = "Enqueue"
  def recentPosts = "Recent posts"
  def reply = "Reply"
  def settings = "Settings"
  def places = "Places"
  def imageSize = "Image size"
  def submit = "Submit"
  def randomPosts = "Random posts"
  def imageQuality = "Image quality"
  def deleteConfirmation(hash: String) = s"Delete post $hash?"
  def writeYourMessage = "Write your message"
  def containerGenerationError = "Container generation failure"
  def attachmentGenerationError = "Attachment generation error"
  def bytes = "bytes"
  def postingError = "Posting error"
  def updateError = "Update error"
  def fromTo(from: Int, to: Int) = s"From $from to $to"
  def embeddedImage = "Embedded image"
  def fileNotSelected = "Source file not selected"
}
