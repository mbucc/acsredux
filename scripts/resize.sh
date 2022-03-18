#! /bin/sh -e
# Created: 3/13/2022
# Resize images and add a black border.
#     * * * * * $HOME/bin/resize.sh

L=$HOME/log/resize.log
D=$HOME/web/template/static/members
for f0 in $(find $D -name '*.orig.jpeg'); do
	w=$(identify -format "%w" $f0)
	h=$(identify -format "%h" $f0)

	# x300 is portrait mode, cap height to 300.
	# 300x is landscape mode, cap width to 300.
	[ $h -gt $w ] && orientation="x300" || orientation="300x"

	f1=$(echo $f0|sed 's/\.orig\./.std./')
	n0=$(stat -c%s $f0)
	n1=$(stat -c%s $f1)
	if [ $n0 -eq $n1 ]; then
		echo resizing $f0 from ${w}x${h} to $orientation >> $L
		/usr/bin/convert $f0 -resize $orientation -bordercolor black -border 3x3 -interlace NONE $f1
	fi
done