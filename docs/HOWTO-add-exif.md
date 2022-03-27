How to add exif data to a photo with imagemagick
=================================================

March 8, 2022

Use https://exiftool.org.

> ExifTool prioritizes the following types of meta information when writing:
>
> 1) EXIF,   2) IPTC,   3) XMP
>
> Many tag names are valid for more than one of these groups. If a group 
> name is not specified when writing information, then the information is 
> added only to the highest priority group for which the tag name is valid 
> (however, the information is updated in all groups where the tag already existed).


No exif data in current photo.
------------------------------------------------

    FN=./cypress/fixtures/10138-80-prospect-business-hours-medium.jpeg
    identify -format '%[EXIF:*]' $FN


exif enforces formatting rules&mdash;and 1998-02-09 is not valid.
------------------------------------------------

    exiftool -CreateDate=1998-02-09 $FN
    Warning: Invalid date/time (use YYYY:mm:dd HH:MM:SS[.ss][+/-HH:MM|Z]) in ExifIFD:CreateDate (PrintConvInv)
    0 image files updated
    1 image files unchanged


The correct CreateDate format is "1998:02:09 06:49:00".
------------------------------------------------

    exiftool -CreateDate="1998:02:09 06:49:00" $FN
    1 image files updated


Now Exif has date, but the field is DateTimeDigitized.
------------------------------------------------

    identify -format '%[exif:*]' $FN                                                                        
    exif:ColorSpace=65535
    exif:ComponentsConfiguration=1, 2, 3, 0
    exif:DateTimeDigitized=1998:02:09 06:49:00
    exif:ExifOffset=90
    exif:ExifVersion=48, 50, 51, 50
    exif:FlashPixVersion=48, 49, 48, 48
    exif:ResolutionUnit=2
    exif:XResolution=180/1
    exif:YCbCrPositioning=1
    exif:YResolution=180/1


Changed JS code to recognize "DateTimeDigitized" and 
Java code to parse "1998:02:09 06:49:00".







Mogrify recipe in stack overflow did not work
=================================================

I think this recipe adds profile data not exif data. 
https://stackoverflow.com/a/230480/1789168.


cat > t    
2#55#Date Created="2000-01-01 12:00" 
^D
mogrify -profile 8BIMTEXT:t $FN


The spec for the tags used in the text.txt file is
here https://exiftool.org/TagNames/IPTC.html:

> The tags ... are part of the International Press Telecommunications Council (IPTC)
> and the Newspaper Association of America (NAA) Information Interchange Model (IIM).