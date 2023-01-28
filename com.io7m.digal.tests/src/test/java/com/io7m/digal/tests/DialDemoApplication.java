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

package com.io7m.digal.tests;

import com.io7m.digal.core.DialBoundedDoubleConverter;
import com.io7m.digal.core.DialBoundedDoubleSnappingConverter;
import com.io7m.digal.core.DialBoundedLongConverter;
import com.io7m.digal.core.DialControl;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.function.Consumer;

/**
 * A demo application for the dial.
 */

public final class DialDemoApplication extends Application
{
  public DialDemoApplication()
  {

  }

  @Override
  public void start(
    final Stage stage)
  {
    final var pane = new FlowPane(Orientation.HORIZONTAL);
    pane.setPrefSize(640, 480);
    pane.setPadding(new Insets(8));

    final var children = pane.getChildren();
    children.add(dial(d -> {
      d.setTickCount(24);
      d.setValueConverter(new DialBoundedLongConverter(0L, 24L, 1L));
      d.setConvertedValue(0.0);
    }));

    children.add(dial(d -> {
      d.setTickCount(20);
      d.setValueConverter(new DialBoundedDoubleConverter(-1.0, 1.0, 0.1));
      d.setConvertedValue(0.0);
    }));

    children.add(dial(d -> {
      d.setTickCount(16 * 2);
      d.setValueConverter(new DialBoundedDoubleSnappingConverter(-8.0, 8.0, 0.5));
      d.setConvertedValue(0.0);
    }));

    final var scene = new Scene(pane);
    stage.setTitle("Dial Control");
    stage.setScene(scene);
    stage.show();
  }

  private static Pane dial(
    final Consumer<DialControl> configurator)
  {
    final var dialSize = 128.0;

    final var dial = new DialControl();
    dial.setPrefSize(dialSize, dialSize);
    dial.setMinSize(dialSize, dialSize);
    dial.setMaxSize(dialSize, dialSize);
    dial.dialRadialGaugeSize().setValue(12.0);

    configurator.accept(dial);

    final var field = new TextField();
    field.setMaxSize(64, 16);
    field.setTranslateY(96.0);
    field.setEditable(false);
    field.setAlignment(Pos.CENTER);
    field.setFont(Font.font("Monospaced", 10));
    field.textProperty().bind(dial.convertedValue().map(Object::toString));

    final var region = new Region();
    region.setPrefSize(128.0 + 16.0, 128.0 + 56);
    region.setMinSize(128.0 + 16.0, 128.0 + 56);
    region.setMaxSize(128.0 + 16.0, 128.0 + 56);
    return new StackPane(region, dial, field);
  }
}
