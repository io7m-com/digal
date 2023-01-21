/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.digal.core;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.function.Function;

/**
 * A convenient dial with a labelText and text field.
 */

public final class DialControlLabelled extends VBox
{
  private static final Insets TEXT_MARGIN_TOP =
    new Insets(8.0, 0.0, 0.0, 0.0);
  private static final Insets PADDING =
    new Insets(0.0, 8.0, 8.0, 8.0);
  private static final double LABEL_HEIGHT =
    32.0;
  private static final double TEXT_HEIGHT =
    24.0;

  private final Label label;
  private final DialControl dial;
  private final TextField text;
  private final ObjectProperty<Function<Number, String>> valueFormatter;

  /**
   * A dial with a labelText and text field.
   */

  public DialControlLabelled()
  {
    this("");
  }

  /**
   * A dial with a labelText and text field.
   *
   * @param initialLabel The initial labelText
   */

  public DialControlLabelled(
    final String initialLabel)
  {
    this.valueFormatter =
      new SimpleObjectProperty<>(Object::toString);

    this.label =
      new Label(initialLabel);
    this.dial =
      new DialControl();
    this.text =
      new TextField();

    this.label.setPrefHeight(LABEL_HEIGHT);

    this.text.setPrefHeight(TEXT_HEIGHT);
    this.text.setEditable(false);
    this.text.setAlignment(Pos.CENTER);
    this.label.setAlignment(Pos.CENTER);

    VBox.setMargin(this.text, TEXT_MARGIN_TOP);

    this.setPadding(PADDING);
    this.setAlignment(Pos.CENTER);

    /*
     * Make the component widths match this layout.
     */

    this.dial.prefWidthProperty()
      .bind(this.prefWidthProperty());
    this.label.prefWidthProperty()
      .bind(this.prefWidthProperty());
    this.text.prefWidthProperty()
      .bind(this.prefWidthProperty());

    /*
     * Make the dial's height match its own width.
     */

    this.dial.prefHeightProperty()
      .bind(this.dial.widthProperty());

    this.text.textProperty()
      .bind(this.dial.internalConvertedValue()
              .map(number -> this.valueFormatter.get().apply(number)));

    this.getChildren()
      .addAll(this.label, this.dial, this.text);
  }

  /**
   * A function that formats the value from the dial into a string used for the
   * dial's text field.
   *
   * @return The text field formatter
   */

  public ObjectProperty<Function<Number, String>> valueFormatter()
  {
    return this.valueFormatter;
  }

  /**
   * @return The dial's label
   */

  public Label label()
  {
    return this.label;
  }

  /**
   * @return The dial label text
   */

  public StringProperty labelText()
  {
    return this.label.textProperty();
  }

  /**
   * @return The dial
   */

  public DialControl dial()
  {
    return this.dial;
  }
}
