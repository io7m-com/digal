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

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Control;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * A rotary dial control.
 */

public final class DialControl extends Region
{
  private static final StyleablePropertyFactory<DialControl> CSS_FACTORY =
    new StyleablePropertyFactory<>(Control.getClassCssMetaData());

  private static final CssMetaData<DialControl, Color> CSS_DIAL_TICK_COLOR =
    CSS_FACTORY.createColorCssMetaData(
      "dial-tick-color",
      s -> s.tickColor,
      Color.gray(0.0, 0.5),
      false
    );
  private static final CssMetaData<DialControl, Number> CSS_DIAL_TICK_SIZE =
    CSS_FACTORY.createSizeCssMetaData(
      "dial-tick-size",
      s -> s.tickSize,
      Double.valueOf(0.5),
      false
    );
  private static final CssMetaData<DialControl, Color> CSS_DIAL_SHADE_COLOR =
    CSS_FACTORY.createColorCssMetaData(
      "dial-shade-color",
      s -> s.shadeColor,
      Color.gray(0.0, 0.125),
      false
    );
  private static final CssMetaData<DialControl, Color> CSS_DIAL_EMBOSS_COLOR =
    CSS_FACTORY.createColorCssMetaData(
      "dial-emboss-color",
      s -> s.embossColor,
      Color.gray(1.0, 0.25),
      false
    );
  private static final CssMetaData<DialControl, Number> CSS_DIAL_EMBOSS_SIZE =
    CSS_FACTORY.createSizeCssMetaData(
      "dial-emboss-size",
      s -> s.embossSize,
      Double.valueOf(3.0),
      false
    );
  private static final CssMetaData<DialControl, Color> CSS_DIAL_RADIAL_GAUGE_COLOR =
    CSS_FACTORY.createColorCssMetaData(
      "dial-radial-gauge-color",
      s -> s.radialGaugeColor,
      Color.DEEPSKYBLUE,
      false
    );
  private static final CssMetaData<DialControl, Number> CSS_DIAL_RADIAL_GAUGE_SIZE =
    CSS_FACTORY.createSizeCssMetaData(
      "dial-radial-gauge-size",
      s -> s.radialGaugeSize,
      Double.valueOf(4.0),
      false
    );
  private static final CssMetaData<DialControl, Color> CSS_DIAL_INDICATOR_COLOR =
    CSS_FACTORY.createColorCssMetaData(
      "dial-indicator-color",
      s -> s.indicatorColor,
      Color.WHITE,
      false
    );
  private static final CssMetaData<DialControl, Number> CSS_DIAL_INDICATOR_SIZE =
    CSS_FACTORY.createSizeCssMetaData(
      "dial-indicator-size",
      s -> s.indicatorSize,
      Double.valueOf(1.5),
      false
    );
  private static final CssMetaData<DialControl, Color> CSS_DIAL_BODY_COLOR =
    CSS_FACTORY.createColorCssMetaData(
      "dial-body-color",
      s -> s.bodyColor,
      Color.gray(0.4, 1.0),
      false
    );
  private static final CssMetaData<DialControl, Color> CSS_DIAL_BODY_STROKE_COLOR =
    CSS_FACTORY.createColorCssMetaData(
      "dial-body-stroke-color",
      s -> s.bodyStrokeColor,
      Color.gray(0.0, 1.0),
      false
    );
  private static final CssMetaData<DialControl, Number> CSS_DIAL_BODY_STROKE_SIZE =
    CSS_FACTORY.createSizeCssMetaData(
      "dial-body-stroke-size",
      s -> s.bodyStrokeSize,
      Double.valueOf(1.0),
      false
    );

  private static final List<CssMetaData<?, ?>> CSS_PROPERTIES =
    List.of(
      CSS_DIAL_BODY_COLOR,
      CSS_DIAL_BODY_STROKE_COLOR,
      CSS_DIAL_BODY_STROKE_SIZE,
      CSS_DIAL_EMBOSS_COLOR,
      CSS_DIAL_EMBOSS_SIZE,
      CSS_DIAL_INDICATOR_COLOR,
      CSS_DIAL_INDICATOR_SIZE,
      CSS_DIAL_RADIAL_GAUGE_COLOR,
      CSS_DIAL_RADIAL_GAUGE_SIZE,
      CSS_DIAL_SHADE_COLOR,
      CSS_DIAL_TICK_COLOR,
      CSS_DIAL_TICK_SIZE
    );

  private static final double PREFERRED_SIZE = 64.0;
  private static final double DRAG_DELTA = 0.005;

  private final Canvas canvas;
  private final Rectangle clip;
  private final SimpleIntegerProperty tickCount;
  private final SimpleStyleableObjectProperty<Color> bodyColor;
  private final SimpleStyleableObjectProperty<Color> bodyStrokeColor;
  private final SimpleStyleableObjectProperty<Color> embossColor;
  private final SimpleStyleableObjectProperty<Color> indicatorColor;
  private final SimpleStyleableObjectProperty<Color> radialGaugeColor;
  private final SimpleStyleableObjectProperty<Color> shadeColor;
  private final SimpleStyleableObjectProperty<Color> tickColor;
  private final SimpleStyleableObjectProperty<Number> bodyStrokeSize;
  private final SimpleStyleableObjectProperty<Number> embossSize;
  private final SimpleStyleableObjectProperty<Number> indicatorSize;
  private final SimpleStyleableObjectProperty<Number> radialGaugeSize;
  private final SimpleStyleableObjectProperty<Number> tickSize;
  private final HashSet<ReadOnlyProperty<?>> properties;
  private final SimpleDoubleProperty internalValueRaw;
  private final SimpleDoubleProperty internalValueConverted;
  private final SimpleDoubleProperty externalValueRaw;
  private final SimpleDoubleProperty externalValueConverted;
  private DialValueConverterType converter;
  private double dragYThen;
  private boolean dragging;

  /**
   * A rotary dial control.
   */

  public DialControl()
  {
    this.converter =
      new DialIdentityConverter();
    this.internalValueRaw =
      new SimpleDoubleProperty();
    this.internalValueConverted =
      new SimpleDoubleProperty();

    this.externalValueRaw =
      new SimpleDoubleProperty();
    this.externalValueConverted =
      new SimpleDoubleProperty();

    this.properties =
      new HashSet<>();

    this.bodyColor =
      propertyOf(this, CSS_DIAL_BODY_COLOR);
    this.bodyStrokeColor =
      propertyOf(this, CSS_DIAL_BODY_STROKE_COLOR);
    this.bodyStrokeSize =
      propertyOf(this, CSS_DIAL_BODY_STROKE_SIZE);
    this.embossColor =
      propertyOf(this, CSS_DIAL_EMBOSS_COLOR);
    this.embossSize =
      propertyOf(this, CSS_DIAL_EMBOSS_SIZE);
    this.indicatorColor =
      propertyOf(this, CSS_DIAL_INDICATOR_COLOR);
    this.indicatorSize =
      propertyOf(this, CSS_DIAL_INDICATOR_SIZE);
    this.radialGaugeColor =
      propertyOf(this, CSS_DIAL_RADIAL_GAUGE_COLOR);
    this.radialGaugeSize =
      propertyOf(this, CSS_DIAL_RADIAL_GAUGE_SIZE);
    this.shadeColor =
      propertyOf(this, CSS_DIAL_SHADE_COLOR);
    this.tickColor =
      propertyOf(this, CSS_DIAL_TICK_COLOR);
    this.tickSize =
      propertyOf(this, CSS_DIAL_TICK_SIZE);

    this.tickCount =
      new SimpleIntegerProperty(2);

    this.setPrefSize(PREFERRED_SIZE, PREFERRED_SIZE);

    this.clip = new Rectangle();
    this.canvas = new Canvas(this.getPrefWidth(), this.getPrefHeight());
    this.canvas.setPickOnBounds(true);
    this.canvas.setClip(this.clip);

    this.getChildren().setAll(this.canvas);

    /*
     * When the internal raw value is updated, the internal converted
     * value is updated.
     */

    this.internalValueRaw.addListener(o -> {
      final var v = this.internalValueRaw.get();
      this.internalValueConverted.set(this.doConversionFromDial(v));
    });

    /*
     * When the external raw value is updated, the external converted
     * value is updated.
     */

    this.externalValueRaw.addListener(o -> {
      final var v = this.externalValueRaw.get();
      this.externalValueConverted.set(this.doConversionFromDial(v));
    });

    /*
     * When any of the listed properties change, the dial is redrawn.
     */

    this.properties.add(this.tickCount);
    this.properties.add(this.widthProperty());
    this.properties.add(this.heightProperty());
    this.properties.add(this.internalValueRaw);

    for (final var p : this.properties) {
      p.addListener(o -> this.redraw());
    }
    this.properties.clear();

    this.dragYThen = 0.0;
    this.canvas.setOnMousePressed(this::onMousePressed);
    this.canvas.setOnMouseDragged(this::onMouseDragged);
    this.canvas.setOnMouseReleased(this::onMouseReleased);
    this.canvas.setOnScroll(this::onMouseScrolled);

    this.internalValueRaw.set(1.0);
    this.internalValueRaw.set(0.0);
  }

  private static <T> SimpleStyleableObjectProperty<T> propertyOf(
    final DialControl control,
    final CssMetaData<DialControl, T> metadata)
  {
    final var name =
      metadata.getProperty();
    final var prop =
      new SimpleStyleableObjectProperty<>(metadata, control, name);
    prop.setValue(metadata.getInitialValue(control));
    control.properties.add(prop);
    return prop;
  }

  private static double clampNormal(
    final double x)
  {
    return Math.min(Math.max(0.0, x), 1.0);
  }

  private double doConversionFromDial(
    final double v)
  {
    if (this.converter instanceof DialValueConverterDiscreteType discrete) {
      return (double) discrete.convertFromDial(v);
    }
    if (this.converter instanceof DialValueConverterRealType real) {
      return real.convertFromDial(v);
    }
    throw new IllegalStateException("Unrecognized converter type.");
  }

  private double doConversionToDial(
    final double x)
  {
    if (this.converter instanceof DialValueConverterDiscreteType discrete) {
      return discrete.convertToDial((long) x);
    }
    if (this.converter instanceof DialValueConverterRealType real) {
      return real.convertToDial(x);
    }
    throw new IllegalStateException("Unrecognized converter type.");
  }

  /**
   * @return The dial body color
   */

  public SimpleStyleableObjectProperty<Color> dialBodyColor()
  {
    return this.bodyColor;
  }

  /**
   * @return The dial body stroke color
   */

  public SimpleStyleableObjectProperty<Color> dialBodyStrokeColor()
  {
    return this.bodyStrokeColor;
  }

  /**
   * @return The dial emboss color
   */

  public SimpleStyleableObjectProperty<Color> dialEmbossColor()
  {
    return this.embossColor;
  }

  /**
   * @return The dial's radial gauge size
   */

  public SimpleStyleableObjectProperty<Number> dialRadialGaugeSize()
  {
    return this.radialGaugeSize;
  }

  @Override
  public List<CssMetaData<? extends Styleable, ?>> getCssMetaData()
  {
    final var rs = new ArrayList<>(getClassCssMetaData());
    rs.addAll(CSS_PROPERTIES);
    return rs;
  }

  private void renderDialIndicator(
    final GraphicsContext g,
    final double halfWidth,
    final double halfHeight,
    final double gaugeSize,
    final double valueNow)
  {
    g.save();
    try {
      final var color = this.indicatorColor.getValue();
      Objects.requireNonNull(color, "color");
      final var width = this.indicatorSize.getValue();
      Objects.requireNonNull(width, "width");

      g.setFill(null);
      g.setStroke(color);
      g.setLineWidth(width.doubleValue());
      g.setLineCap(StrokeLineCap.ROUND);
      g.translate(halfWidth, halfHeight);
      g.rotate(-225.0 + (valueNow * 270.0));

      g.strokeLine(
        0.0,
        0.0,
        halfWidth - (gaugeSize * 2.0),
        0.0
      );
    } finally {
      g.restore();
    }
  }

  private void renderDialEmboss(
    final GraphicsContext g,
    final double width,
    final double height,
    final double gaugeSize)
  {
    final var color = this.embossColor.getValue();
    Objects.requireNonNull(color, "color");
    final var size = this.embossSize.getValue();
    Objects.requireNonNull(size, "size");

    final var sizeD = size.doubleValue();
    g.setFill(null);
    g.setStroke(color);
    g.setLineWidth(sizeD);
    g.strokeOval(
      gaugeSize + sizeD,
      gaugeSize + sizeD,
      width - ((2.0 * sizeD) + (gaugeSize * 2.0)),
      height - ((2.0 * sizeD) + (gaugeSize * 2.0)));
  }

  private void renderDialBody(
    final GraphicsContext g,
    final double width,
    final double height,
    final double gaugeSize)
  {
    final var fillColor = this.bodyColor.getValue();
    Objects.requireNonNull(fillColor, "fillColor");
    final var strokeColor = this.bodyStrokeColor.getValue();
    Objects.requireNonNull(strokeColor, "strokeColor");
    final var strokeSize = this.bodyStrokeSize.getValue();
    Objects.requireNonNull(strokeSize, "strokeSize");

    g.setFill(fillColor);
    g.setStroke(strokeColor);
    g.setLineWidth(strokeSize.doubleValue());

    g.fillOval(
      gaugeSize,
      gaugeSize,
      width - (gaugeSize * 2.0),
      height - (gaugeSize * 2.0));
    g.strokeOval(
      gaugeSize,
      gaugeSize,
      width - (gaugeSize * 2.0),
      height - (gaugeSize * 2.0));
  }

  private void renderTickMarks(
    final GraphicsContext g,
    final double halfWidth,
    final double halfHeight)
  {
    g.save();
    try {
      final var color = this.tickColor.getValue();
      Objects.requireNonNull(color, "color");
      final var size = this.tickSize.getValue();
      Objects.requireNonNull(color, "size");

      g.setFill(null);
      g.setStroke(color);
      g.setLineWidth(size.doubleValue());
      g.setLineCap(StrokeLineCap.ROUND);
      g.translate(halfWidth, halfHeight);

      final var count = this.tickCount.get();
      final var tickDelta = 270.0 / (double) count;

      g.rotate(-225.0);
      for (int index = 0; index <= count; ++index) {
        g.strokeLine(0.0, 0.5, halfWidth, 0.5);
        g.rotate(tickDelta);
      }

    } finally {
      g.restore();
    }
  }

  private void renderShadow(
    final GraphicsContext g,
    final double width,
    final double height)
  {
    final var color = this.shadeColor.getValue();
    Objects.requireNonNull(color, "color");

    g.setStroke(null);
    g.setFill(color);
    g.fillOval(0.0, 0.0, width, height);
  }

  private void renderRadialGauge(
    final GraphicsContext g,
    final double width,
    final double height,
    final double valueNow)
  {
    final var color = this.radialGaugeColor.getValue();
    Objects.requireNonNull(color, "color");

    final var arcExtent = -(valueNow * 270.0);
    g.setStroke(null);
    g.setFill(color);
    g.fillArc(
      0.0, 0.0, width, height, 225.0, arcExtent, ArcType.ROUND
    );
  }

  /**
   * Set the raw value of the dial, in the range {@code [0,1]}. If the user is
   * currently dragging the dial, the update will be ignored. Observers of the
   * various value properties will not be called, but the UI will be updated to
   * reflect the new value.
   *
   * @param x The value
   *
   * @see #rawValue()
   * @see #convertedValue()
   */

  public void setRawValueQuietly(
    final double x)
  {
    if (this.dragging) {
      return;
    }

    this.setInternalRawValue(x);
  }

  /**
   * Set the raw value of the dial, in the range {@code [0,1]}. If the user is
   * currently dragging the dial, the update will be ignored. Observers of the
   * various value properties will be notified.
   *
   * @param x The value
   *
   * @see #rawValue()
   * @see #convertedValue()
   */

  public void setRawValue(
    final double x)
  {
    if (this.dragging) {
      return;
    }

    this.setInternalRawValue(x);
    this.setExternalRawValue(x);
  }

  /**
   * Set the value of the dial in display units (according to the registered
   * converter). Observers of the various value properties will not be called,
   * but the UI will be updated to reflect the new value.
   *
   * @param x The display value
   *
   * @see #setValueConverter(DialValueConverterType)
   */

  public void setConvertedValueQuietly(
    final double x)
  {
    this.setRawValueQuietly(this.doConversionToDial(x));
  }

  /**
   * Set the value of the dial in display units (according to the registered
   * converter). Observers of the various value properties will be notified.
   *
   * @param x The display value
   *
   * @see #setValueConverter(DialValueConverterType)
   */

  public void setConvertedValue(
    final double x)
  {
    this.setRawValue(this.doConversionToDial(x));
  }

  /**
   * @return The current raw value
   */

  public double getRawValue()
  {
    return this.internalValueRaw.getValue().doubleValue();
  }

  /**
   * @return The current converted value
   */

  public double getConvertedValue()
  {
    return this.internalValueConverted.getValue().doubleValue();
  }

  private void setInternalRawValue(
    final double x)
  {
    this.internalValueRaw.set(clampNormal(x));
  }

  private void setExternalRawValue(
    final double x)
  {
    this.externalValueRaw.set(clampNormal(x));
  }

  /**
   * @return The number of tick marks shown on the dial
   *
   * @see #setTickCount(int)
   */

  public ReadOnlyIntegerProperty tickCount()
  {
    return this.tickCount;
  }

  /**
   * @return The current dial value converted according to the registered value
   * converter
   *
   * @see #setValueConverter(DialValueConverterType)
   */

  public ReadOnlyDoubleProperty convertedValue()
  {
    return this.externalValueConverted;
  }

  /**
   * @return The current raw dial value
   */

  public ReadOnlyDoubleProperty rawValue()
  {
    return this.externalValueRaw;
  }

  /**
   * Set the value converter for the dial.
   *
   * @param f The value converter
   */

  public void setValueConverter(
    final DialValueConverterType f)
  {
    this.converter = Objects.requireNonNull(f, "f");
  }

  private void onMousePressed(
    final MouseEvent mouseEvent)
  {
    if (mouseEvent.getButton() != MouseButton.PRIMARY) {
      return;
    }

    this.dragging = true;
    this.dragYThen = mouseEvent.getSceneY();
  }

  private void onMouseDragged(
    final MouseEvent mouseEvent)
  {
    if (mouseEvent.getButton() != MouseButton.PRIMARY) {
      return;
    }

    /*
     * Dragging a dial updates both the internal and external value.
     */

    this.dragging = true;

    final var dragYNow =
      mouseEvent.getSceneY();
    final var delta =
      dragYNow - this.dragYThen;

    final var valueThen =
      this.internalValueRaw.get();

    final double valueNow;
    if (delta > (double) 0) {
      valueNow = valueThen - DRAG_DELTA;
    } else {
      valueNow = valueThen + DRAG_DELTA;
    }

    this.setInternalRawValue(valueNow);
    this.setExternalRawValue(valueNow);

    this.dragYThen = dragYNow;
  }

  private void onMouseReleased(
    final MouseEvent mouseEvent)
  {
    if (mouseEvent.getButton() != MouseButton.PRIMARY) {
      return;
    }

    this.dragging = false;
  }

  private void onMouseScrolled(
    final ScrollEvent scrollEvent)
  {
    final var valueThen =
      this.internalValueRaw.get();

    final var delta =
      scrollEvent.getDeltaY();

    final double valueNow;
    if (delta > 0.0) {
      valueNow = this.doConversionToNextDial(valueThen);
    } else if (delta < 0.0) {
      valueNow = this.doConversionToPreviousDial(valueThen);
    } else {
      return;
    }

    this.setInternalRawValue(valueNow);
    this.setExternalRawValue(valueNow);
  }

  private double doConversionToPreviousDial(
    final double x)
  {
    if (this.converter instanceof DialValueConverterDiscreteType discrete) {
      return discrete.convertToDial(
        discrete.convertedPrevious(discrete.convertFromDial(x))
      );
    }
    if (this.converter instanceof DialValueConverterRealType real) {
      return real.convertToDial(
        real.convertedPrevious(real.convertFromDial(x))
      );
    }
    throw new IllegalStateException("Unrecognized converter.");
  }

  private double doConversionToNextDial(
    final double x)
  {
    if (this.converter instanceof DialValueConverterDiscreteType discrete) {
      return discrete.convertToDial(
        discrete.convertedNext(discrete.convertFromDial(x))
      );
    }
    if (this.converter instanceof DialValueConverterRealType real) {
      return real.convertToDial(
        real.convertedNext(real.convertFromDial(x))
      );
    }
    throw new IllegalStateException("Unrecognized converter.");
  }

  private void redraw()
  {
    final var g = this.canvas.getGraphicsContext2D();

    final var width = this.getWidth();
    final var height = this.getHeight();
    final var halfWidth = width / 2.0;
    final var halfHeight = height / 2.0;

    this.canvas.setWidth(width);
    this.canvas.setHeight(height);
    this.clip.setWidth(width);
    this.clip.setHeight(height);

    final var radialGaugeSizeV = this.radialGaugeSize.getValue();
    Objects.requireNonNull(radialGaugeSizeV, "radialGaugeSizeV");
    final var gaugeSize = radialGaugeSizeV.doubleValue();

    g.clearRect(0.0, 0.0, width, height);

    /*
     * Take the converted value and convert it back to a raw value. The
     * reason for doing this is that the converter may apply some kind of
     * value snapping (such as snapping to integer values), and we want the
     * dial to visually snap to values.
     */

    final var ic =
      this.internalValueConverted.get();
    final var valueNow =
      this.doConversionToDial(ic);

    this.renderRadialGauge(g, width, height, valueNow);
    this.renderShadow(g, width, height);
    this.renderTickMarks(g, halfWidth, halfHeight);
    this.renderDialBody(g, width, height, gaugeSize);
    this.renderDialEmboss(g, width, height, gaugeSize);
    this.renderDialIndicator(g, halfWidth, halfHeight, gaugeSize, valueNow);
  }

  /**
   * Set the number of tick marks that appear on the dial (up to a maximum of
   * 270).
   *
   * @param i The tick mark count
   */

  public void setTickCount(
    final int i)
  {
    this.tickCount.set(Math.max(1, Math.min(270, i)));
  }

  /**
   * @return The current (internal) dial value converted according to the
   * registered value converter
   *
   * @see #setValueConverter(DialValueConverterType)
   */

  public ReadOnlyDoubleProperty internalConvertedValue()
  {
    return this.internalValueConverted;
  }

  /**
   * @return The current (internal) raw dial value
   */

  public ReadOnlyDoubleProperty internalRawValue()
  {
    return this.internalValueRaw;
  }
}
