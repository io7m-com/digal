digal
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.digal/com.io7m.digal.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.digal%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.digal/com.io7m.digal?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/digal/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/digal.svg?style=flat-square)](https://codecov.io/gh/io7m-com/digal)

![com.io7m.digal](./src/site/resources/digal.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/digal/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/digal/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/digal/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/digal/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/digal/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/digal/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/digal/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/digal/actions?query=workflow%3Amain.windows.temurin.lts)|

## Digal

A customizable, scalable JavaFX rotary dial.

![digal](src/site/resources/dial0.png)

### Features

* CSS-styleable rotary dials
* Scalable to any size
* [OSGi](http://www.osgi.org)-ready
* [JPMS](https://en.wikipedia.org/wiki/Java_Platform_Module_System)-ready
* High coverage automated test suite
* ISC license

### Maven

```
<dependency>
  <groupId>com.io7m.digal</groupId>
  <artifactId>com.io7m.digal.core</artifactId>
  <version>${latest}</version>
</dependency>
```

### What Is A Dial?

A dial is a rotary knob seen on hardware such as guitar amplifiers, mixing
desks, and etc.

![digal](src/site/resources/desk.jpg)

In `digal`, a dial carries a real value in the range `[0, 1]` where `0` means "
turned fully anti-clockwise" and `1` means "turned fully clockwise". Each dial
can be provided with a value converter that converts this internal value to
something else for display purposes.

Dials are constrained to a `270°` range in order to unambiguously indicate the
current value at a glance.

Visually, a dial consists of the following components:

![digal](src/site/resources/dial1.png)

* The _indicator_ is a small notch on the dial that shows which direction the
  dial is pointing.
* The _radial gauge_ is a filled-in arc segment showing how far away the current
  dial setting is from the minimum.
* The _tick marks_ are small marks around the dial that can be used to indicate
  discrete values. The number of tick marks can be customized.
* The _shade_ emulates the way dials are often recessed into hardware. The shade
  can be disabled.
* The _body_ is the actual physical dial.

The text field below the dial is _not_ part of the dial itself, and is a plain
text field bound the dial value for demonstration purposes.

Dials are manipulated by clicking the body and dragging upwards and downwards
on the Y axis. Dragging upwards turns the dial clockwise, and dragging downwards
turns the dial anti-clockwise.

### Value Converters

As mentioned, dials use real values in the range `[0,1]`. A dial instance
can be provided with a _value converter_ that allows for converting internal
values to something else for use externally. The package comes with a number
of built-in converters, organized into _real_ and _discrete_ converters. A
_real_ converter maps dial values in the range `[0, 1]` to a user-defined
range of real numbers. A _discrete_ converter maps dial values in the range
`[0, 1]` to a user-defined range of integers.

```
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
      return x + 0.5;
    }

    @Override
    public double convertedPrevious(
      final double x)
    {
      return x - 0.5;
    }
  });
```

Note that it would probably also be a good idea to set the number of tick
marks to `12`:

```
dial0.setTickCount(12);
```

### Data Flow

Some applications may choose to use dials both as a data display and a data
input. For example, a user interface that controls an external audio device
might want dials to always match the state of the external device, but also
need to be adjustable by the user turning the dial onscreen. The dials are
typically configured with a `ChangeListener` that is invoked when the user
turns the dial that submits commands to update the external device. Additionally,
the dials are usually set to particular values when state updates are received
from the external device. This can cause a problem due to a circular data
dependency:

  1. The user turns a dial.
  2. The `ChangeListener` on the dial sees the dial value change and submits
     a command to update the value on the external device.
  3. The external device returns the newly set value.
  4. The dial is updated with the new value.
  5. The `ChangeListener` on the dial sees the dial value change and submits
     a command to update the value on the external device.
  6. The external device returns the newly set value...

This problem is sometimes mitigated by the fact that setting a JavaFX property
to a value to which it is already set doesn't result in observers of the
property being called. This isn't always reliable, however, and so the
`digal` API provides "quiet" versions of the commands to update dials that
break the cycle of observer updates.

The `setRawValueQuietly` and `setConvertedValueQuietly` commands will set
the value of a dial and update the dial's UI, but _will not_ call any observers
of the dial's value property.

In the scenario described above, state updates that come from the external
audio device should set dial values using `set*Quietly` so that the state
updates do not cause more commands to be submitted to the device.

### CSS

The dial components can be customized to some extent with CSS. Assuming
a dial with id `#dial0`, the following CSS will produce an ugly looking dial:

```
#dial0
{
  dial-body-color: #30a030;
  dial-body-stroke-color: #0000ff;
  dial-emboss-color: #00000030;
  dial-emboss-size: 4.0;
  dial-indicator-color: #00ffff;
  dial-indicator-size: 3.0;
  dial-radial-gauge-color: #ff00ff;
  dial-radial-gauge-size: 12.0;
  dial-shade-color: #ff000050;
  dial-tick-color: #ff0000;
  dial-tick-size: 1.0;
}
```

![ugly](com.io7m.digal.tests/src/main/resources/com/io7m/digal/tests/dial.png)

