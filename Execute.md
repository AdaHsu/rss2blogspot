# Introduction #

Using rss2blogspot, you can import your rss feed into Blogger.com !

# Step 1: Get your Blogger ID #

  * Log into your Blogger account and goto your blogger dashboard, You can get a blog list which what you managed.
  * Click the 'New Post' or 'Edit Posts' and the browser bring us to **EDIT** mode, checking your navigation bar, you will get a link like 'http://www.blogger.com/post-create.g?blogID= **7742357521630360685** '.
  * This parameter **blogID** is what we need, in this case the blogID is **7742357521630360685** .

# Step 2: Import your RSS Feed #

# **In this time, rss2blogspot just can import only one post from one feed.**

  * Unpack rss2blogspot-0.1-[r4](https://code.google.com/p/rss2blogspot/source/detail?r=4).rar , you can build your own package or just use the prepackaged jar file at **target/RSS2blogspot.jar** .
  * Copy RSS2blogspot.jar to your rss feed's directory.
  * Type as follow:<br> <code> java -jar RSS2blogspot.jar &lt;google account&gt; &lt;google password&gt; &lt;blogID&gt; &lt;rss filename&gt; </code>
<ul><li>After that, you can find your post shown on Blogger.com.