# Content Design

March 23, 2022

Tweaking content model presented in 
[Chapter 6](https://philip.greenspun.com/seia/content-management) 
of Software Engineering for Internet Applications (SEIA).


1. photo diary data structure supports any time duration: (from, upto)
2. web adapter can render based on duration; e.g,
     * \> 6 months --> break into months.
     * < 2 months && > 15 days --> break into weeks.
     * etc.
     * initially this
       logic can be hardcoded to an annual diary.
3. diary entries are also content rows, and so also have a from/upto
     * photos are at a point in time (from = upto)
     * text covers a date range.
     * again, web renderer will break this into sections; e.g,
         - if text duration = diary duration, then it is page intro up top
         - if text duration = month, then it spans that month
         - if text duration = a point, then it is just at that one spot.
4. if we want to use D3 for a timeline view, same data structure will work.
   just renderer has to change.
5. this content data model will also support events.
6. Regular articles will be markdown.  The image (url, not bytes) 
   will be a child content because we will want
   to use an image in an article that is not in a diary.
7. Add a web filter that adds a links to the large image whenever an
   image is on the site.

In SEIA, the release time and expiration time 
model news content, which has a release date
and expires.  We won't represent that concept here.  From
and upto only describe the date/time range the content refers
to.

The other date time field is CreatedOn, which represents when the
content was added to the site.  For example, if photo take in March
was uploaded in April, the CreatedOn is April, the FromDateTime is 
March and the UptoDateTime is March.