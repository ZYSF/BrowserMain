# BrowserMain
A *very* minimalist Web Browser &amp; HTML Engine

![Screenshot](/screenshot-0.0.1.png)

## Features

* Basic browser shell with URL entry and "go" button
* Renders basic HTML elements (not with any styling or other refined layout yet, just bare minimum)
* Basic form support (*just* enough to use Google and DuckDuckGo - although they will be rendered horribly)
* Basic image support (uses the underlying Java platform's image loading)

In other words, this is just enough to access some simple websites and follow links between them, but won't render the contents very nicely or support all features (and there's probably zero hope of accessing more complex sites like Facebook).

It may be useful as a starting point for a better browser or rendering engine.

## Dependencies

NOTE: This is an upload of an Eclipse project without it's dependencies. This means you'll have to set up the build yourself.
If the project becomes more serious in the future I'll try to streamline it a bit but for now this is just a toy browser so you'll have to tinker yourself!

 * Java (~1.8+)
 * jSoup for HTML processing
 * Apache HTTPClient for loading content
 * [This](https://github.com/phil-brown/jCSS-Parser) CSS parser may be required in future builds

## License

"You can do anything you wanna do" ~ Phil Lynott

(Copyright is not my thing, you can use the UNLICENSE text for legal purposes. Dependencies have their own licenses.)
