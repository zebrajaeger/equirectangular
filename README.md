# equirectangular

## Overview
This is a tool to wrap spherical panoramic images (esp. from Kolor Autopano, http://www.kolor.com/) 
with a border to make it equirectangular.

## Why should i do this?
The tool krPano (http://krpano.com/) can generate a viewer to see the panoramic images in a first-person view.
But when the images are not equirectangular, it creates a flash-based viewer. Otherwise a htlm(5) only player 
(and a flash player too).
This tool makes krPano able to generate a htlm5-multiresolution-partial-pano you can see on Desktop, smartphone etc.
 
## How to use it?
She Program is written in java. So you need a java runtime environment you can get from Oracle (http://www.oracle.com/technetwork/java/javase/downloads/index.html)

### Autopano
The easyest way ist to use it with Autopano because it puts all needed metadata into the rendered panoramic image.
This tool can read the embedded XMP-Metadata and use it for generation.

java -jar equirectangular.jar <myimage>

### Another programn
At this time you need to know, how much percent of a full circle the image is width
You also can specify the difference from the middle for vertical offset (in percent)

usage: java -jar equirectangular [Options] <sourcefile>
 -D,--delete               delete target file if exists
 -h,--help                 show this help
 -l,--log-level <arg>      the log-level. One of TRACE, DEBUG,
                           INFO(default), WARNING, ERROR
 -o,--out <arg>            the target file. Without this option a file
                           with a new filename is generated in the
                           source-directory
 -r,--dry-run              make a dry run: no changing file calls will
                           made
 -w,--target-width <arg>   the width of target-image in percent
 -y,--offset-y <arg>       the y difference from middle in percent
 

# Which image-formats are supported?
Photoshop PSD and PSB Files (http://www.adobe.com/devnet-apps/photoshop/fileformatashtml/)