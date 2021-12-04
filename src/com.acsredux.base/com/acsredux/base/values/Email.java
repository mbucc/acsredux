package com.acsredux.base.values;

import static com.acsredux.base.Util.dieIfBlank;

public record Email(String value) {
  public Email(String value) {
    dieIfBlank(value, "email");
    // https://stackoverflow.com/questions/8204680/java-regex-email
    // Hard problem, but let's do something reasonable if imperfect.
    //   var pat = "^([\\p{L}-_\\.]+){1,64}"  // \\p     POSIX character class
    //                                    //     \\p{L}  any Unicode letter
    //                                    //     [\\p{L}_\\.] any letter and '_' or '.'
    //                                    //    {1,64} from 1 to 64 letters (not bytes!)
    //  + "@([\\p{L}&&[^_]]+)"            // a domain name component
    //                                    // can be 1 or more Unicode characters
    //                                    // except an '_'
    //  + "{2,255}\."                     //
    //
    // https://en.wikipedia.org/wiki/Domain_name#Domain_name_syntax
    // The hierarchy of domains descends from the right to the left label
    // in the name; each label to the left specifies a subdivision, or
    // subdomain of the domain to the right. For example: the label example
    // specifies a node example.com as a subdomain of the com domain, and
    // www is a label to create www.example.com, a subdomain of example.com.
    // Each label may contain from 1 to 63 octets. The empty label is
    // reserved for the root node and when fully qualified is expressed
    // as the empty label terminated by a dot. The full domain name may
    // not exceed a total length of 253 ASCII characters in its textual
    // representation.[8] Thus, when using a single character per label,
    // the limit is 127 levels: 127 characters plus 126 dots have a total
    // length of 253. In practice, some domain registries may have shorter
    this.value = value;
  }
}
