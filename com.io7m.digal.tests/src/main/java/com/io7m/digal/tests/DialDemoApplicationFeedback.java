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

import com.io7m.digal.core.DialControl;
import com.io7m.digal.core.DialValueConverterRealType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A demo application for the dial.
 */

public final class DialDemoApplicationFeedback extends Application
{
  private final ScheduledExecutorService executor;

  public DialDemoApplicationFeedback()
  {
    this.executor =
      Executors.newSingleThreadScheduledExecutor(r -> {
        final var thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
      });
  }

  @Override
  public void start(
    final Stage stage)
  {
    final var pane = new VBox();
    pane.setPrefSize(640, 480);
    pane.setPadding(new Insets(8));

    final var dial0 = new DialControl();
    final var dialSize = 128.0;
    dial0.setPrefSize(dialSize, dialSize);
    dial0.setMinSize(dialSize, dialSize);
    dial0.setMaxSize(dialSize, dialSize);

    dial0.dialRadialGaugeSize().setValue(12.0);

    dial0.setTickCount(24);
    dial0.setValueConverter(
      new DialValueConverterRealType()
      {
        @Override
        public double convertToDial(
          final double x)
        {
          return x / 12.0;
        }

        @Override
        public double convertFromDial(
          final double x)
        {
          return (double) Math.round(x * 12.0);
        }

        @Override
        public double convertedNext(
          final double x)
        {
          return x + 1.0;
        }

        @Override
        public double convertedPrevious(
          final double x)
        {
          return x - 1.0;
        }
      });

    final var field = new TextArea();
    field.setMaxSize(320, 240);
    field.setTranslateY(16.0);
    field.setEditable(false);
    field.setFont(Font.font("Monospaced", 10));

    dial0.convertedValue()
      .addListener((observable, oldValue, newValue) -> {
        final var lines =
          new ArrayList<>(
            Arrays.stream(field.getText().split("\n"))
              .toList()
          );

        while (lines.size() > 10) {
          lines.remove(0);
        }

        lines.add(newValue.toString());
        field.setText(String.join("\n", lines));
      });

    dial0.convertedValue()
      .addListener((observable, oldValue, newValue) -> {
        this.executor.schedule(() -> {
          Platform.runLater(() -> {
            dial0.setConvertedValueQuietly(newValue.doubleValue());
          });
        }, 100L, TimeUnit.MILLISECONDS);
      });

    pane.getChildren()
      .addAll(dial0, field);

    final var scene = new Scene(pane);
    stage.setTitle("Dial Control");
    stage.setScene(scene);
    stage.show();
  }
}
