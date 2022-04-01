Update DOM with JavaScript and then take an action in the same function
=======================================================================

April 1, 2022

What: When someone clicks the "delete image" button, highlight the 
image before displaying a confirm dialog.

How: After you update the DOM put the action
on the event queue using setTimeout so the current call stack clears.

WHy: When JavaScript updates the DOM, the browser does not render the
new DOM until the method currently running is done.

Note: this (and browser reflows) is probably one reason why libraries like React were written.

More details in the `test/javascript-event-loop` directory.

Sample code
--------------------------------------------

    <script>
      function rmdiv(id) {
        let y = document.getElementById(id);
        y.style.border = "solid red";
        setTimeout(function(){
          console.log("start");
          let start = new Date();
          // See note on strange Safari behavior in test directory
          // mentioned at the top of this HOWTO.
          let waitInSeconds = 0.01;
          let end = new Date(start.valueOf() + waitInSeconds * 1000);
          do {start = new Date();} while (end-start > 0);
          if (confirm("Delete the selected image?")) {
            let xhr = new XMLHttpRequest();
            xhr.open("GET", "http://example.com");
            xhr.onreadystatechange = function () {
              if (xhr.readyState === XMLHttpRequest.DONE) {
                console.log(xhr.status);
                console.log(xhr.responseText);
              }};
            xhr.send();
          }
          y.style.border = "initial";
          console.log("done");
        },10);
      }
    </script>