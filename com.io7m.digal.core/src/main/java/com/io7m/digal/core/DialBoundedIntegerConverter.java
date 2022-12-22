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

public final class DialBoundedIntegerConverter
  implements DialValueConverterType
{
  private final int minInclusive;
  private final int maxInclusive;

  /**
   * A value converter that converts to/from a bounded integer range.
   *
   * @param inMinInclusive The inclusive minimum value
   * @param inMaxInclusive The inclusive maximum value
   */

  public DialBoundedIntegerConverter(
    final int inMinInclusive,
    final int inMaxInclusive)
  {
    this.minInclusive = inMinInclusive;
    this.maxInclusive = inMaxInclusive;

    if (inMaxInclusive < inMinInclusive) {
      throw new IllegalArgumentException(
        "Minimum inclusive %d must be < maximum inclusive %d"
          .formatted(
            Integer.valueOf(inMinInclusive),
            Integer.valueOf(inMaxInclusive))
      );
    }
  }

  @Override
  public double convertToDial(
    final double x)
  {
    final var dMin =
      (double) this.minInclusive;
    final var dMax =
      (double) this.maxInclusive;

    return (x - dMin) / (dMax - dMin);
  }

  @Override
  public double convertFromDial(
    final double x)
  {
    final var dMin =
      (double) this.minInclusive;
    final var dMax =
      (double) this.maxInclusive;
    final var delta =
      dMax - dMin;

    return (x * delta) + dMin;
  }
}
