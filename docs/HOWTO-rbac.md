Role-Based Access Control
============================

December 2, 2021

I'd like to experiment with separating the security policy from the 
rest of the business logic.  Move the complexity into a policy engine that 
reads in a policy declaration and 
outputs a data structure that makes enforcement uniform and simple.
Inspired by the blog post
[RBAC like it was meant to be](https://tailscale.com/blog/rbac-like-it-was-meant-to-be/)
by Avery Pennarun at tailscale.

What is a security policy?  Here are some examples:
1. A site admin can disable an abusive user.
2. An author can give other people the ability to edit their work.
3. A member can restrict who sees their profile to a set of trusted friends.
4. An anonymous user (for example, a search engine) should not see email addresses.

Terms
--------------------------------------------------------------------------------

```
User          a person or a device that acts on a resource.
              
Tenant        A customer.  Subjects are associated with one or more customers.

              In a "pooled, multi-tenant" application, the data for
              different customers is stored in the same database.
              In a "silo" design, each tenant get's their own
              database. See https://docs.aws.amazon.com/wellarchitected/latest/saas-lens/tenant.html.

Role          a collection of users within a tenant.
              An administrator sets roles on hire, promotion or transfer.
              
              The original sense of this term has business meaning; think
              "Support Engineer" not "report-reader".
              
              Also called a Group.

Resource      the "object" with restricted access.
              A "User" acts on a "Resource."
              (In original RBAC, a "subject" acts on an "object".)
              
Action        What actions you can take with a resource.
              For example, "edit", "view", "read", "write", "export".

Entitlement   a subject-action-object rule.
              More commmonly called a Permission.

ACL           a list of entitlements.
              Short for "Access Control List"
              Every resource has one and only one ACL.

Tag           a resource attribute, with an admin-assigned owner.
              The resource owner may assign any tag or tags they own
              when they create a new resource.
              
              A "purpose-based identity."
```


Different Approaches and their problems.
--------------------------------------------------------------------------------

My summary of the tailscale blog post.



| Style               | ACL                                                                                                                                                                                                                    | Problems                                                                                                                                                                                                                                                                                                                                                                 |
|---------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Windows File System | Each file lists Users that can take each action                                                                                                                                                                        | ACL is on the file and often (in the real world) you have to track down individual files to change entitlements.                                                                                                                                                                                                                                                         |
| One group per ACL   | e.g., report-readers and report-writers.                                                                                                                                                                               | You need a set of groups for each distinct ACL combination.  This number of groups can get really big.  <br><br>Pressure to give many people access to IAM access is strong.  <br><br>Roles have lost the meaning of why a person should have the role---they are too low-level.  If someone changes jobs, it can be hard to figure out the corresponding group changes. |
| Resource Tags       | Tag a file as "Report" or "Database"<br><br>ACL links groups to tag + action.<br><br>Each object owner can grant access to their objects, but which access they can grant is controlled by the overall security policy |                                                                                                                                                                                                                                                                                                                                                                          |

Stating the policy for the examples above.
--------------------------------------------------------------------------------

Can we define a static policy file that covers the examples above?

**1. A site admin can disable an abusive user.**

To write an ACL, you need three things: user, action, resource.  In this case,

1. The resource is any member.
2. The action is "disable".
3. The users that can do this are the site administrators.

Using a syntax similar to tailscale, this policy is:

```
{
  "acls": [
    // Admins can disable any member.
    {
      "resource": ["member:*"],
      "action": "disable",
      "users": ["role:admins"],
    },
  ],
  // A static declaration. Not data-driven.
  "roles": {
    "role:admins": [
      "alice@example.com",
      "bob@example.com",
    ],
  }
}
```

**2. An author can give other people the ability to edit their work.**

Instead of regenerating a static policy file each time a new user creates an
account, we'll introduce an `{{email}}` placeholder which indicates the email
of the member that is logged on.

The resource is anything content the author assigned the edit tag to.

This approach could be extended to allocate multiple tags to each user and
allow them to give them tag names that make sense to them.

```
{
  "acls": [
    // A member can assign edit rights to the articles they write.
    {
      "resource": ["article:tag:{{email}}:edit"],
      "action": "edit",
      "users": ["tag:{{email}}:edit"],
    },
  ],
  "tagOwners": {
    "tag:{{email}}:edit": [{{email}}]
  }
}
```


**3. A member can restrict who sees their profile to a set of trusted friends.**

The resource is the user's profile page.

```
{
  "acls": [
    {
      "resource": ["member:{{email}}"],
      "action": "view",
      "users": ["tag:{{email}}:view"],
    },
  ],
  "tagOwners": {
    "tag:{{email}}:view": [{{email}}]
  } 
}
```


**4. An anonymous user (for example, a search engine) should not see email addresses.**

The resource is a particular field of a member.

```
{
  "acls": [
    // Only logged in users should be able to see emails.
    // The logged in tag is associated with the subject by the system.
    {
      "resource": [ "member:*:email" ],
      "action": "view",
      "users": ["tag:loggedin"],
    },
  ]
}
```

Approach
-------------------------------------------------------------------------------

Require a Subject in every Service call (javax.security.auth.Subject).

Define a Principal that holds tags and groups (java.security.Principal).

Wrap each service in a dynamic proxy (java.lang.reflect.Proxy).

For each query:
1. Determine entity returned (don't bother with value objects)
2. Remove entities the subject does not have read access to.
3. Replace entity attributes with "****" if subject does not have access to.

For each command:
1. Determine action (use annotation?  fail if annotation missing?).
2. Determine entity (specify when proxy created?)
3. Return 403 if subject does not have the entitlement.




Background: DAC, MAC, ABAC and RBAC
-------------------------------------------------------------------------------

Notes from the tailscale blog post.

```
 DAC     Discretionary Access Control       Unix files (kind-of).
         Resource owner can grant access.


 MAC     Mandatory Access Control           a daemon binds to a port.
         An admin grants access.            a mandatory file lock
                                            true two-factor
                                            (second factor cannot be shared)

                                            Original idea: concentric circles.
                                            DoD "multi-level security"

                                            Now: flags or subgroups (SELinux)

                                            "nightmarishly complicated to actually
                                            use, unless you are the NSA (who wrote
                                            SELinux).  And maybe even if you are."

                                            "both too restrictive and too vague."

 ABAC    Attribute-Based Access Control     Checks user role + attributes
         A refinement of RBAC.              reCAPTCHA uses "logged in" attribute.
         (Hu, Kuhn, and Ferraiolo, 2015)
                                            "a slightly more convoluted RBAC"

                                            attributes "mostly implemented
                                            centrally in your identity provider"

 RBAC    Role-Based Access Control          "You probably haven’t tried real RBAC."
         A subset of MAC.
         (Ferraiolo and Kuhn, 1992)         "Nowadays Linux also supports facls,
                                            which are RBAC, but nobody knows how
                                            to use facls, so they don’t count."
```