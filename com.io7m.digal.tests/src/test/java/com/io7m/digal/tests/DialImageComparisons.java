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

import com.github.romankh3.image.comparison.ImageComparison;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public final class DialImageComparisons
{
  private DialImageComparisons()
  {

  }

  static void compareSampleImageWithScene(
    final String imageName,
    final Scene scene,
    final double allowedDifferencePercentage)
    throws InterruptedException, IOException
  {
    final var imageReceived =
      new WritableImage(
        (int) scene.getWidth(),
        (int) scene.getHeight()
      );

    final var latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      scene.snapshot(param -> {
        latch.countDown();
        return null;
      }, imageReceived);
    });
    latch.await(5L, TimeUnit.SECONDS);

    final var imageReceivedOutput =
      new BufferedImage(
        (int) imageReceived.getWidth(),
        (int) imageReceived.getHeight(),
        BufferedImage.TYPE_INT_ARGB
      );

    final var graphics = imageReceivedOutput.createGraphics();
    final var reader = imageReceived.getPixelReader();
    for (int y = 0; y < (int) imageReceived.getHeight(); ++y) {
      for (int x = 0; x < (int) imageReceived.getWidth(); ++x) {
        final var argb = reader.getArgb(x, y);
        final var a = (argb >> 24) & 0xff;
        final var r = (argb >> 16) & 0xff;
        final var g = (argb >> 8) & 0xff;
        final var b = (argb & 0xff);
        graphics.setPaint(new Color(r, g, b, a));
        graphics.fillRect(x, y, 1, 1);
      }
    }
    graphics.dispose();

    ImageIO.write(
      imageReceivedOutput,
      "PNG",
      new File(imageName)
    );

    final var imageExpected =
      loadSampleImage(imageName);

    final var imageComparison =
      new ImageComparison(imageExpected, imageReceivedOutput);

    final var imageComparisonResult =
      imageComparison.compareImages();

    final var difference =
      imageComparisonResult.getDifferencePercent();

    assertTrue(
      difference < allowedDifferencePercentage,
      String.format(
        "Difference %f must be < %f",
        Float.valueOf(difference),
        Double.valueOf(allowedDifferencePercentage))
    );
  }

  private static BufferedImage loadSampleImage(
    final String imageName)
    throws IOException
  {
    final var stream =
      DialControlTest.class.getResource(
        "/com/io7m/digal/tests/%s".formatted(imageName)
      );

    return ImageIO.read(stream);
  }
}
