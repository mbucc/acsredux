picker.onchange = evt => {
    const [file] = picker.files
    if (file) {
        preview.src = URL.createObjectURL(file)

        var x = document.getElementById('preview')
        x.style.display = "block";

        //
        //              Set photo orientation and date.
        //
        const tags = ExifReader
            .load(file)
            .then(function (tags) {
                //
                //      Set orientation
                //
                if (tags.hasOwnProperty('Image Height') && tags.hasOwnProperty('Image Width')) {
                    const h = tags['Image Height'].value;
                    const w = tags['Image Width'].value;
                    var x = document.getElementById('imageOrientation')
                    x.value = 'PORTRAIT';
                    if (w > h) {
                        x.value = 'LANDSCAPE';
                    }
                }

                //
                //      Set date if it exists, otherwise enable date picker.
                //
                //          DateTimeOriginal is the the time the image was created.
                //          CreateDate is the time the file was created.  In a digital
                //          camera, they should be about the same.
                //      -- ref: https://exiftool.org/forum/index.php?topic=10283.0
                //
                if (tags.hasOwnProperty('CreateDate')) {
                    var x = document.getElementById("imageDateTime");
                    x.value = tags['CreateDate'].description;
                } else if (tags.hasOwnProperty('DateTimeDigitized')) {
                    var x = document.getElementById("imageDateTime");
                    x.value = tags['DateTimeDigitized'].description;
                } else {
                    var x = document.getElementById("imageDatePicker");
                    x.style.display = "block";
                    x.required = true;
                    x = document.getElementById("imageDatePickerLabel");
                    x.style.display = "block";
                    alert("We could not extract a date from the image.  " +
                      "We've added a date picker to the form, " +
                      "please use it to enter the date the picture was taken.")
                }
            }).catch(function (error) {
                console.log("error");
                console.log(error);
                var x = document.getElementById("imageDatePicker");
                x.style.display = "block";
                x = document.getElementById("imageDatePickerLabel");
                x.style.display = "block";
            });

    }
}