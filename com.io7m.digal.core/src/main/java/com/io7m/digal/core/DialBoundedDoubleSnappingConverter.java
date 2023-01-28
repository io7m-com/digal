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
 * A value converter that converts to/from a bounded double range.
 */

public final class DialBoundedDoubleSnappingConverter
  implements DialValueConverterRealType
{
  private final double minInclusive;
  private final double maxInclusive;
  private final double increment;

  /**
   * A value converter that converts to/from a bounded double range.
   *
   * @param inMinInclusive The inclusive minimum value
   * @param inMaxInclusive The inclusive maximum value
   * @param inIncrement    The increment value
   */

  public DialBoundedDoubleSnappingConverter(
    final double inMinInclusive,
    final double inMaxInclusive,
    final double inIncrement)
  {
    this.minInclusive = inMinInclusive;
    this.maxInclusive = inMaxInclusive;
    this.increment = inIncrement;

    if (inMaxInclusive < inMinInclusive) {
      throw new IllegalArgumentException(
        "Minimum inclusive %f must be < maximum inclusive %f"
          .formatted(
            Double.valueOf(inMinInclusive),
            Double.valueOf(inMaxInclusive))
      );
    }
  }

  @Override
  public double convertToDial(
    final double x)
  {
    final var y = (double) Math.round(x / this.increment) * this.increment;
    final var dMin = this.minInclusive;
    return (y - dMin) / (this.maxInclusive - dMin);
  }

  @Override
  public double convertFromDial(
    final double x)
  {
    return this.fromDial(this.convertToDial(this.fromDial(x)));
  }

  private double fromDial(
    final double x)
  {
    final var dMin =
      this.minInclusive;
    final var delta =
      this.maxInclusive - dMin;

    return (x * delta) + dMin;
  }

  @Override
  public double convertedNext(
    final double x)
  {
    return x + this.increment;
  }

  @Override
  public double convertedPrevious(
    final double x)
  {
    return x - this.increment;
  }
}
