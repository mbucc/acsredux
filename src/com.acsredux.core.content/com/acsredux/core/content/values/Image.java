package com.acsredux.core.content.values;

public record Image(
  ImageSource source,
  ImageOrientation orientation,
  ImageDate takenOn,
  AltText altText
)
  implements SectionElement {}
