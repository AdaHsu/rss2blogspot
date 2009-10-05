/*
 * RSS2blogspot - Import your rss feeds to blogspot.com ( Blogger.com )
 * 
 * How to use this ? Please visit http://code.google.com/p/rss2blogspot/w/list !
 * 
 * Copyright (c) 2009 Ada Hsu. ada@adahsu.net
 * 
 * This utility use of the software CC-GNU GPL 
 * (http://creativecommons.org/licenses/GPL/2.0/) 
 * authorization, You can view the relevant provisions of 
 * http://www.gnu.org/copyleft/gpl.html text.
 * 
 * You are free to use the software, but software maintainer 
 * will hold no liability for any damages, Thank You.
 * 
 * Reversion: $Rev$
 * Author: $Author$
 * Date: $Date$
 * Id: $Id$
 *
 */
package net.adahsu.rss2blogspot;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Enumeration;
import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Appender;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import com.google.gdata.client.blogger.BloggerService;
import com.google.gdata.data.Category;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Entry;
import com.google.gdata.data.Feed;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.blogger.BlogEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ParseException;
import com.google.gdata.util.ServiceException;

public class RSS2blogspot {

	// logger for message
	private static Logger logger = null;

	private static final String FEED_URI_BASE = "http://www.blogger.com/feeds";
	private static final String METAFEED_URL = FEED_URI_BASE + "/default/blogs";
	private static final String POSTS_FEED_URI_SUFFIX = "/posts/default";
	private static final String COMMENTS_FEED_URI_SUFFIX = "/comments/default";
	private static final String ATOM_SCHEMA = "http://www.blogger.com/atoms/ns#";
	private static String feedUri = null;

	private static final String appVersion = "0.1";
	private static final String appName = "net.adahsu.rss2blogger.RSS2Blogger";

	private String account = null;
	private String password = null;
	private String blogId = null;
	private boolean authenticated = false;
	private boolean draft = false;

	// RSS Namespace
	private Namespace nsContent = Namespace.getNamespace("content",
			"http://purl.org/rss/1.0/modules/content/");
	private Namespace nsDC = Namespace.getNamespace("dc",
			"http://purl.org/dc/elements/1.1/");

	// blogger service
	private BloggerService service = null;

	// rss document
	private Document rssDoc = null;

	// Constructor
	public RSS2blogspot() throws IOException {

		log4jConfig();

		service = getBloggerService();

	}

	// Constructor
	public RSS2blogspot(String imported, String filterouted) throws IOException {

		log4jConfig();

		service = getBloggerService();
	}

	private void log4jConfig() {

		if (logger == null) {

			BasicConfigurator.configure();

			Enumeration e = Logger.getRootLogger().getAllAppenders();

			for (; e.hasMoreElements();) {
				Appender a = (Appender) e.nextElement();
				a.setLayout(new PatternLayout("[%-5p] %C{1}: %m%n"));
			}

			logger = Logger.getLogger(this.getClass());
			logger.setLevel(Level.INFO);

		}
	}

	private BloggerService getBloggerService() {

		return new BloggerService(appName + "-" + appVersion);
	}

	// make sure we can save rss file to the directory.
	private void checkDirectory(File dir) throws IOException {

		if (dir.exists() && !dir.isDirectory()) {
			logger.fatal(dir.getName() + " must be a directory !");
			throw new IOException(dir.getName() + " must be a directory !");
		}

		if (!dir.exists()) {
			try {
				dir.mkdir();
			} catch (Exception e) {
				logger.fatal("Can not create directory: " + dir.getName(), e);
				throw new IOException(e.getMessage());
			}
		}
	}

	public void printParameter() {

		logger.info("");
		logger.info("===================================");
		logger.info("Blog ID: "
				+ ((this.blogId == null) ? "<none>" : this.blogId));
		logger.info("Account: " + this.account);
		logger.info("Draft mode: " + draft);
		logger.info("===================================");
	}

	/**
	 * check the user can manage some blogID
	 * 
	 * @throws AuthenticationException
	 * 
	 */
	public Boolean authenticate(String account, String pwd, String blogId)
			throws AuthenticationException, ServiceException, IOException {

		this.account = account;
		this.password = pwd;
		this.blogId = blogId;

		service.setUserCredentials(this.account, this.password);
		authenticated = true;

		return ownedBlogId();
	}

	private Boolean ownedBlogId() throws AuthenticationException,
			ServiceException, IOException {

		if (!authenticated || service == null) {
			logger.error("Please login first... ! ");
			throw new AuthenticationException("Have not logined !");
		}

		URL feedUrl = null;

		try {
			feedUrl = new URL(METAFEED_URL);
		} catch (MalformedURLException mue) {
			logger.fatal("METAFEED URL format error !", mue);
			System.exit(-1);
		}

		Feed result = null;

		result = service.getFeed(feedUrl, Feed.class);

		List<Entry> entries = result.getEntries();
		int cnt = entries.size();

		if (cnt == 0) {
			logger.info("User " + this.account + " has no any blog site. ");
			return false;
		}

		if (cnt == 1) {
			Entry entry = entries.get(0);
			this.blogId = entry.getId().split("blog-")[1];
			logger.info("User " + this.account
					+ " has only on blog site, blog ID is " + this.blogId);
			return true;
		}

		if (this.blogId == null) {
			logger.info("User " + this.account
					+ " has blog site list as follow. ");
			logger.info("=====================================");
			for (int i = 0; i < cnt; i++) {
				Entry entry = entries.get(i);
				String name = entry.getTitle().getPlainText();
				String id = entry.getId().split("blog-")[1];
				System.out.println("\tName: " + name + ", \tID: " + id);
			}
			return false;
		}

		for (int i = 0; i < cnt; i++) {
			Entry entry = entries.get(i);
			if (this.blogId.equals(entry.getId().split("blog-")[1])) {
				logger.info("User " + this.account
						+ " can manage this blog site: " + this.blogId);
				return true;
			}
		}

		logger.info("User " + this.account
				+ " have no any authentication to manage the blog site: "
				+ this.blogId);
		return false;

	}

	private Boolean validate() {

		if (rssDoc == null) {
			return false;
		}

		Element root = rssDoc.getRootElement();

		// root element is not <rss>
		if (!"rss".equals(root.getName())) {
			return false;
		}

		// must only one channel
		if (root.getChildren("channel").size() != 1) {
			return false;
		}

		// channel must had 1 or more <item>
		Element channel = root.getChild("channel");
		if (channel.getChildren("item").size() == 0) {
			return false;
		}

		logger.info("RSS file confirmed !");
		return true;
	}

	/**
	 * Load RSS file into memory
	 * 
	 * @param rssFilename
	 * @return
	 */
	public Boolean loadRSS(String rssFilename) {

		SAXBuilder builder = new SAXBuilder();
		try {
			rssDoc = builder.build(new File(rssFilename));

		} catch (Exception e) {
			logger.error(e);
			return false;
		}

		return validate();
	}

	private Pattern[] getForceDraftPattern(Element element) {

		ArrayList<Pattern> forceList = new ArrayList();
		Pattern[] sample = new Pattern[1];

		Element forceDraft = element.getChild("forceDraft");
		if (forceDraft == null) {
			logger.info("No forceDraft setting... ");
			return null;
		}

		List list = forceDraft.getChildren("pattern");
		for (int i = 0; i < list.size(); i++) {
			Element e = (Element) list.get(i);
			String p = e.getTextTrim();
			Pattern pattern = Pattern.compile(p);
			forceList.add(pattern);
		}

		return forceList.toArray(sample);

	}

	private String[] getRemoveString(Element element) {

		ArrayList<String> stringList = new ArrayList();
		String[] sample = new String[1];

		Element remove = element.getChild("remove");
		if (remove == null) {
			logger.info("No removable setting... ");
			return null;
		}

		List list = remove.getChildren("pattern");
		for (int i = 0; i < list.size(); i++) {
			Element e = (Element) list.get(i);
			String s = e.getTextTrim();
			stringList.add(s);
		}

		return stringList.toArray(sample);
	}

	private HashMap getReplaceConfig(Element element) {

		HashMap map = new HashMap();

		Element replaces = element.getChild("replaces");
		if (replaces != null) {
			List list = replaces.getChildren("pattern");
			for (int i = 0; i < list.size(); i++) {
				Element e = (Element) list.get(i);
				Element match = e.getChild("regex");
				Element replace = e.getChild("replacement");
				if (match == null) {
					map.put(e.getTextTrim(), "");
				} else {
					String s = (replace == null) ? "" : replace.getTextTrim();
					map.put(match.getTextTrim(), s);
				}
			}
		}

		return map;
	}

	public static String decode(String encodeHtml) {

		return encodeHtml.replace("&lt;", "<").replace("&gt;", ">").replace(
				"&quot;", "\"").replace("&amp;", "&");
	}

	public int transform(String rssFilename, boolean isDraft)
			throws IOException {

		int transformed = 0;
		this.draft = isDraft;

		URL postUrl = null;

		try {
			postUrl = new URL(FEED_URI_BASE + "/" + blogId
					+ POSTS_FEED_URI_SUFFIX);
		} catch (MalformedURLException mue) {
			logger.fatal("FEED_URI format error !", mue);
			System.exit(-1);
		}

		if (loadRSS(rssFilename)) {

			Element rssRoot = rssDoc.getRootElement();
			Element channel = rssRoot.getChild("channel");

			// Get post parameter
			// if tag <draft> is setted, overwrite it
			if (channel.getChildText("draft") != null) {
				draft = Boolean.parseBoolean(channel.getChildText("draft"));
			}

			// filterout = getPattern( rssRoot );
			Pattern[] forcePattern = getForceDraftPattern(channel);
			// String[] removeString = getRemoveString( channel );
			HashMap replaceMap = getReplaceConfig(channel);

			logger.info("Starting to transform... ");

			List items = channel.getChildren("item");
			for (int i = 0; i < items.size(); i++) {
				Element e = (Element) items.get(i);

				BlogEntry entry = new BlogEntry();

				String content = e.getChildText("encoded", nsContent);
				if (content == null) {
					content = e.getChildText("description");
				}
				content = decode(content);

				// replaced what we want to replaced
				if (replaceMap.size() > 0) {
					Iterator it = replaceMap.keySet().iterator();
					while (it.hasNext()) {
						String regex = (String) it.next();
						String replacement = (String) replaceMap.get(regex);
						content = content.replaceAll(regex, replacement);
					}
				}

				entry.setContent(new PlainTextConstruct(content));

				boolean forceDraft = false;

				String title = e.getChildText("title");

				// match what need to edit again
				if (forcePattern != null) {
					for (int j = 0; j < forcePattern.length; j++) {
						if (forcePattern[j].matcher(content).find()) {
							title = "[" + rssFilename + ":" + j + "] " + title;
							forceDraft = true;
							logger.info("Force Draft: " + title);
							break;
						}
					}
				}

				entry.setTitle(new PlainTextConstruct(title));

				boolean draftFlag = this.draft;
				if (e.getChildText("draft") != null) {
					draftFlag = Boolean.parseBoolean(e.getChildText("draft"));
				}
				draftFlag |= forceDraft;
				entry.setDraft(draftFlag);

				// Published Time
				DateTime publishedTime = null;
				String postPublish = e.getChildText("date", nsDC);
				try {
					if (postPublish != null) {
						publishedTime = DateTime.parseDateTime(postPublish);
					} else {
						postPublish = e.getChildText("pubDate");
						if (postPublish != null) {
							publishedTime = DateTime.parseRfc822(postPublish);
						}
					}
				} catch (ParseException pe) {
					publishedTime = null;
				}

				if (publishedTime.getTzShift() == null) {
					publishedTime.setTzShift(0);
				}
				entry.setPublished(publishedTime);

				// Check iff category elements exists
				List categories = e.getChildren("category");
				for (int j = 0; j < categories.size(); j++) {
					String label = ((Element) categories.get(j)).getTextTrim();
					String[] labels = label.split("/");
					for (int k = 0; k < labels.length; k++) {
						Category category = new Category();
						category.setScheme("http://www.blogger.com/atom/ns#");
						category.setTerm(labels[k]);
						entry.getCategories().add(category);
					}
				}

				// Post new post
				BlogEntry post = null;
				try {
					System.out.println("Begin Post... ");
					post = service.insert(postUrl, entry);
					transformed++;

					logger.info("Post created: "
							+ post.getTitle().getPlainText() + ", "
							+ post.getPublished());
				} catch (Exception exception) {
					logger.error(exception);
				}
			}
		} else {
			String msg = "Can not load rss file " + rssFilename + "! ";
			logger.error(msg);
			throw new IOException(msg);
		}

		logger.info(transformed + " posts created... !");

		return transformed;
	}

	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println();
			System.out.println("Usage: ");
			System.out
					.println("   java -jar RSS2blogspot.jar <account> <password> <blogger Id> <rss file>");
			System.out.println();
			System.exit(-1);
		}

		try {
			RSS2blogspot rss2blogger = new RSS2blogspot();

			String account = args[0];
			String password = args[1];
			String blogId = (args.length > 2) ? args[2] : null;

			rss2blogger.authenticate(account, password, blogId);

			if (args.length > 3) {
				for (int i = 3; i < args.length; i++) {
					rss2blogger.transform(args[i], true);
				}
			}
			rss2blogger.printParameter();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
