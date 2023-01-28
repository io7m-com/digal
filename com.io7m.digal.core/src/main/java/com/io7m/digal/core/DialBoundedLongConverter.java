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

/**
 * A value converter that converts to/from a bounded integer range.
 */

public final class DialBoundedLongConverter
  implements DialValueConverterDiscreteType
{
  private final long minInclusive;
  private final long maxInclusive;
  private final long increment;

  /**
   * A value converter that converts to/from a bounded integer range.
   *
   * @param inMinInclusive The inclusive minimum value
   * @param inMaxInclusive The inclusive maximum value
   * @param inIncrement    The increment value
   */

  public DialBoundedLongConverter(
    final long inMinInclusive,
    final long inMaxInclusive,
    final long inIncrement)
  {
    this.minInclusive = inMinInclusive;
    this.maxInclusive = inMaxInclusive;
    this.increment = inIncrement;

    if (inMaxInclusive < inMinInclusive) {
      throw new IllegalArgumentException(
        "Minimum inclusive %d must be < maximum inclusive %d"
          .formatted(
            Long.valueOf(inMinInclusive),
            Long.valueOf(inMaxInclusive))
      );
    }
  }

  @Override
  public double convertToDial(
    final long x)
  {
    final double dMin = (double) this.minInclusive;
    final double n = (double) x - dMin;
    final double d = (double) this.maxInclusive - dMin;
    return n / d;
  }

  @Override
  public long convertFromDial(
    final double x)
  {
    final var dMin =
      this.minInclusive;
    final var delta =
      this.maxInclusive - dMin;

    return Math.round((x * delta) + dMin);
  }

  @Override
  public long convertedNext(
    final long x)
  {
    return x + this.increment;
  }

  @Override
  public long convertedPrevious(
    final long x)
  {
    return x - this.increment;
  }
}
