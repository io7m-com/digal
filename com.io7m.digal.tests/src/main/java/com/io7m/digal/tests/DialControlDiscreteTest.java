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
import com.io7m.digal.core.DialValueConverterDiscreteType;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.VerticalDirection;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

@ExtendWith(ApplicationExtension.class)
public final class DialControlDiscreteTest
{
  private Stage stageCurrent;

  /**
   * Test that scrolling the dial changes the value.
   *
   * @param robot The FX robot
   * @param info  The test info
   */

  @Test
  public void testDialScroll(
    final FxRobot robot,
    final TestInfo info)
  {
    Platform.runLater(() -> {
      this.stageCurrent.setTitle(
        "%s: %s".formatted(info.getTestClass().get(), info.getDisplayName())
      );
    });

    final DialControl dial =
      robot.lookup("#dial0")
        .query();

    robot.targetWindow(dial)
      .clickOn(dial, MouseButton.PRIMARY);

    robot.point(dial)
      .atOffset(0.0, -32.0);

    robot.scroll(1, VerticalDirection.UP);

    FxAssert.verifyThat(dial, node -> {
      final var x = node.tickCount().get();
      return x == 12;
    });

    FxAssert.verifyThat(dial, node -> {
      final var x = node.convertedValue().get();
      return x == 1.0;
    });

    robot.scroll(1, VerticalDirection.DOWN);

    FxAssert.verifyThat(dial, node -> {
      final var x = node.convertedValue().get();
      return x == 0.0;
    });
  }

  @Start
  public void start(
    final Stage stage)
    throws Exception
  {
    this.stageCurrent = stage;

    final var pane = new StackPane();
    pane.setPrefSize(640, 480);
    pane.setPadding(new Insets(8));

    final var dial0 = new DialControl();
    final var dialSize = 128.0;
    dial0.setPrefSize(dialSize, dialSize);
    dial0.setMinSize(dialSize, dialSize);
    dial0.setMaxSize(dialSize, dialSize);

    dial0.setId("dial0");
    dial0.setTickCount(12);
    dial0.setValueConverter(
      new DialValueConverterDiscreteType()
      {
        @Override
        public double convertToDial(
          final long x)
        {
          return (double) x / 16.0;
        }

        @Override
        public long convertFromDial(
          final double x)
        {
          return Math.round(x * 16.0);
        }

        @Override
        public long convertedNext(
          final long x)
        {
          return x + 1L;
        }

        @Override
        public long convertedPrevious(
          final long x)
        {
          return x - 1L;
        }
      });

    pane.getChildren().addAll(dial0);

    final var scene = new Scene(pane);
    stage.setTitle("Dial Control");
    stage.setScene(scene);
    stage.show();
  }

  @Stop
  public void stop()
    throws Exception
  {

  }
}
