package com.karasiq.nanoboard.frontend.locales

import com.karasiq.nanoboard.api.NanoboardContainer

object English extends BoardLocale {
  def nanoboard = "Nanoboard"
  def generateContainer = "Generate container"
  def cancel = "Cancel"
  def dataContainer = "Source file"
  def dequeue = "Dequeue"
  def insertImage = "Picture"
  def recentPostsFrom(post: Int) = s"Recent posts (from $post)"
  def categories = "Categories"
  def pendingPosts = "Pending posts"
  def containerGeneration = "Container generation"
  def delete = "Delete"
  def imageFormat = "Image format"
  def imageSharpness = "Image sharpness"
  def enqueue = "Enqueue"
  def recentPosts = "Recent posts"
  def reply = "Reply"
  def settings = "Settings"
  def places = "Places"
  def imageScale = "Image scale (%)"
  def imageSize = "Image size (pixels)"
  def useServerRendering = "Use server rendering"
  def preview = "Preview"
  def submit = "Submit"
  def randomPosts = "Random posts"
  def imageQuality = "Image quality"
  def deleteConfirmation(hash: String) = s"Are you sure you want to permanently delete post #$hash?"
  def writeYourMessage = "Write your message"
  def bytes = "bytes"
  def style = "Style"
  def fromTo(from: Int, to: Int) = s"From $from to $to"
  def embeddedImage = "Embedded image"
  def fileNotSelected = "Source file not selected"
  def preferences = "Preferences"
  def control = "Control"
  def offset = "Offset"
  def count = "Count"
  def batchDelete = "Batch delete"
  def batchDeleteConfirmation(count: Int) = s"Are you sure you want to permanently delete $count posts?"
  def batchDeleteSuccess(count: Int) = s"$count posts successfully removed"
  def clearDeleted = "Clear deleted posts"
  def clearDeletedConfirmation = "Clear deleted posts cache?"
  def clearDeletedSuccess(count: Int) = s"$count deleted posts evicted"
  def containers = "Containers"
  def container(c: NanoboardContainer) = s"№${c.id}, ${c.posts} posts"
  def webSocketError = "WebSocket error"
  def clearDeletedError = "Clearing deleted posts failure"
  def containerGenerationError = "Container generation failure"
  def attachmentGenerationError = "Attachment generation error"
  def batchDeleteError = "Batch deletion error"
  def settingsUpdateError = "Settings update error"
  def postingError = "Posting error"
  def updateError = "Update error"
}
