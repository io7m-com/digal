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
 * <p>A converter to and from <i>dial</i> values. A dial value is a real value
 * in the range {@code [0, 1]} and is used to track the position of the dial
 * internally. However, it is often desirable to <i>display</i> values in a
 * range other than {@code [0, 1]}. The <tt>DialValueConverterType</tt>
 * interface allows for performing this conversion.</p>
 *
 * <p>The following invariant should hold:</p>
 * {@code x = convertFromDial(convertToDial(x)) }
 */

public interface DialValueConverterType
{
  /**
   * Convert a value to the range {@code [0, 1]}.
   *
   * @param x The input value
   *
   * @return A value in the range {@code [0, 1]}.
   */

  double convertToDial(
    double x);

  /**
   * Convert a value in the range {@code [0, 1]} to the display range.
   *
   * @param x The input value
   *
   * @return A value in the display range
   */

  double convertFromDial(
    double x);
}
