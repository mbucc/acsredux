function deleteImage(contentID) {
  if (!contentID) {
    console.log("deleteImage: empty contentID");
    return;
  }
  let divid = "imgdiv-" + contentID;
  let el = document.getElementById(divid);
  if (!el) {
    alert("Error: no image found with id '" + divid + "'.");
    return;
  }

  if (confirm("Please confirm you want to delete this image by clicking OK.")) {
    let xhr = new XMLHttpRequest();
    xhr.open("DELETE", "/content/" + contentID);
    xhr.onreadystatechange = function () {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        console.log(xhr.status);
        console.log(xhr.responseText);
        var status = xhr.status;
        if (status === 0 || (status >= 200 && status < 400)) {
          // Note: The remove() function removes the element and all its children.
          el.remove();
        } else {
          alert("Error: " + xhr.responseText);
          el.style.border = "initial";
        }
      }
    };
    console.log("sending XHR");
    xhr.send();
  } else {
    el.style.border = "initial";
  }
}
