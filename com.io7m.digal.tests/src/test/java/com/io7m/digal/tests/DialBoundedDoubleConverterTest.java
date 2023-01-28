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
import com.io7m.digal.core.DialBoundedLongConverter;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class DialBoundedDoubleConverterTest
{
  @Property
  public void test100(
    final @ForAll @DoubleRange(min = (double) 0, max = 100.0) double x)
  {
    final var c =
      new DialBoundedDoubleConverter((double) 0L, 100.0, 1.0);
    final var y =
      c.convertToDial(x);

    assertTrue(y >= 0.0);
    assertTrue(y <= 1.0);

    final var z =
      c.convertFromDial(y);

    assertEquals(x, (double) z, 0.00001);
  }

  @Property
  public void test100n_100(
    final @ForAll @DoubleRange(min = -100.0, max = 100.0) double x)
  {
    final var c =
      new DialBoundedDoubleConverter(-100.0, 100.0, 1.0);
    final var y =
      c.convertToDial(x);

    assertTrue(y >= 0.0);
    assertTrue(y <= 1.0);

    final var z =
      c.convertFromDial(y);

    assertEquals(x, (double) z, 0.00001);
  }

  @Property
  public void testMisordered(
    final @ForAll @DoubleRange(min = (double) 0, max = (double) Integer.MAX_VALUE) double min,
    final @ForAll @DoubleRange(min = (double) Integer.MIN_VALUE, max = -1.0) double max)
  {
    assertThrows(IllegalArgumentException.class, () -> {
      new DialBoundedDoubleConverter(min, max, 1.0);
    });
  }

  @Test
  public void testNextPrevious()
  {
    final var c =
      new DialBoundedDoubleConverter(-100.0, 100.0, 1L);

    assertEquals(1.0, c.convertedNext(0.0));
    assertEquals(0.0, c.convertedPrevious(1.0));
  }
}
