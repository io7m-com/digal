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
import com.io7m.digal.core.DialIdentityConverter;
import com.io7m.digal.core.DialValueConverterRealType;
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

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
public final class DialControlTest
{
  private Stage stageCurrent;

  /**
   * Test that adjusting the dial changes the value.
   *
   * @param robot The FX robot
   * @param info  The test info
   */

  @Test
  public void testDial(
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

    final var target =
      robot.point(dial)
        .atOffset(0.0, -32.0);

    robot.drag(dial, MouseButton.PRIMARY);
    robot.dropTo(target);

    FxAssert.verifyThat(dial, node -> {
      final var x = node.tickCount().get();
      return x == 12;
    });

    FxAssert.verifyThat(dial, node -> {
      final var x = node.convertedValue().get();
      return x == 2.0;
    });

    final var rawValue = new AtomicReference<Double>();

    FxAssert.verifyThat(dial, node -> {
      final var x = node.rawValue().get();
      rawValue.set(Double.valueOf(x));

      final var rounded = Math.floor(x * 100.0) / 100.0;
      return rounded == 0.16;
    }, sb -> {
      sb.append(" (Raw value ");
      sb.append(rawValue.get());
      sb.append(" must be close to 0.16)");
      return sb;
    });
  }

  /**
   * Test that custom CSS changes the appearance.
   *
   * @param robot The FX robot
   * @param info  The test info
   */

  @Test
  public void testCSS(
    final FxRobot robot,
    final TestInfo info)
    throws Exception
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

    dial.getStylesheets()
      .add(DialControlTest.class.getResource("/com/io7m/digal/tests/style.css")
             .toString());

    dial.applyCss();
    dial.setRawValue(0.3);

    robot.sleep(1L, TimeUnit.SECONDS);

    /*
     * Capture an image of the scene and save it.
     */

    DialImageComparisons.compareSampleImageWithScene(
      "testCSS.png",
      dial.getScene(),
      1.5
    );
  }

  /**
   * Setting dial values programmatically can notify or not notify observers.
   *
   * @param robot The FX robot
   * @param info  The test info
   */

  @Test
  public void testNoObservers(
    final FxRobot robot,
    final TestInfo info)
    throws Exception
  {
    Platform.runLater(() -> {
      this.stageCurrent.setTitle(
        "%s: %s".formatted(info.getTestClass().get(), info.getDisplayName())
      );
    });

    final DialControl dial =
      robot.lookup("#dial0")
        .query();

    final var updates = new LinkedList<Double>();
    dial.rawValue()
      .addListener((observable, oldValue, newValue) -> {
        updates.add(Double.valueOf(newValue.doubleValue()));
      });

    dial.setValueConverter(new DialIdentityConverter());
    dial.setRawValue(0.5);
    dial.setRawValueQuietly(0.6);
    dial.setConvertedValue(0.7);
    dial.setConvertedValueQuietly(0.8);

    assertEquals(0.5, updates.poll());
    assertEquals(0.7, updates.poll());
    assertEquals(0, updates.size());

    assertEquals(0.8, dial.getConvertedValue());
    assertEquals(0.8, dial.getRawValue());
  }

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
