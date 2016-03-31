package com.karasiq.nanoboard.frontend.styles

import scalatags.Text.all._

trait Makaba extends BoardStyle {
  override def toString: String = {
    "Makaba"
  }

  override def body = cls(
    color := "#333333",
    backgroundColor := "#EEEEEE"
  )

  override def post = cls(
    minWidth := 40.pct,
    maxWidth := 100.pct,
    border := "solid 1px #CCCCCC",
    borderRadius := 2.px,
    display.`inline-block`,
    background := "#DDDDDD",
    color := "#333333",
    margin := 0.25.em,
    clear.both,
    padding := "0.5em 1.5em"
  )

  override def postInner = cls(
    marginBottom := 0.5.em,
    fontSize := 0.9.em,
    fontFamily := "Verdana,sans-serif"
  )

  override def postId = cls(
    color := "#789922",
    marginRight := 0.5.em
  )

  override def postLink = cls(
    color := "#FF6600",
    &.hover(
      color := "#0066FF"
    ),
    cursor.pointer,
    marginRight := 0.5.em
  )

  override def input = cls()

  override def submit = cls()

  override def greenText = cls(
    color.green,
    fontSize := 90.pct,
    lineHeight := 2.em
  )

  override def spoiler = cls(
    textDecoration.none,
    color := "#BBBBBB",
    background := "#BBBBBB",
    &.hover(
      color := "#333333"
    )
  )
}
