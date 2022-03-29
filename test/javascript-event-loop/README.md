March 29, 2022

How to highlight image you are deleting.
=========================================

> It [JavaScript] will change the dom immediately, but until the callstack
> is clear the UI won't update.
> 
> -- Comment by KevinB in https://stackoverflow.com/a/7342127/1789168 


> The global setTimeout() method sets a timer which executes a function
> or specified piece of code once the timer expires.
> 
> -- https://developer.mozilla.org/en-US/docs/Web/API/setTimeout

Test #4 and #5 were interesting.  I had to add a tiny sleep loop in
order for the confirm dialog to show.

The Safari behavior is strange.
-----------------------------------
The Safari behavior is wierd on test #5.  With the timeout
of 0.01, the border always turns red on the first click.  

But when I click the link a 
second time, the red border does not show.  If I refresh the page 
at this point, 
the red border still does not render the first time I click.

If I up the delay to 0.2 seconds, it works more reliably but not 100%.

 

