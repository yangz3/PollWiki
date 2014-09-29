
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PollWiki
 */
public class PollWiki extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public volatile static int timecount = 0;

	public static int[] numberOfView = new int[10];
	public static String[] nameOfArticle = new String[10];

	/**
	 * @throws Exception
	 * @see HttpServlet#HttpServlet()
	 */
	public PollWiki() throws Exception {
		super();

		// initialize the current ranking arrays
		Arrays.fill(numberOfView, 0);
		Arrays.fill(nameOfArticle, "");

		// start a threading to update current wiki ranking every 30 minutes
		Thread t = new Thread(new MyRunnable());

		t.setDaemon(true); // this makes the Thread die when your application
							// exits!
		t.start();

	}

	/**
	 * @throws IOException
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		if (numberOfView[0] != 0) {
			for (int i = 0; i < 10; i++) {
				response.getWriter().println(
						nameOfArticle[i] + "$$" + numberOfView[i]);
			}
		} else {
			response.getWriter().println("server will begin in 5 minutes");
			response.getWriter().println(timecount);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		// TODO Auto-generated method stub
	}

	class MyRunnable implements Runnable {
		public void run() {
			for (;;) {

				try {

					updateCurrentRank();
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				try {
					Thread.sleep(1000 * 60 * 30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		void updateCurrentRank() throws Exception {

			// create temporary arrays for computation
			// then assign them to the current ranking array at once
			int[] tmp_numberOfView = new int[10];
			String[] tmp_nameOfArticle = new String[10];
			Arrays.fill(tmp_numberOfView, 0);
			Arrays.fill(tmp_nameOfArticle, "");

			// n bubble sort to find the top 10 and store them in the temporary
			// arrays
			for (int i = 0; i < 10; i++) {
				URL u = new URL(getLatestURL());
				InputStream fileStream = u.openStream();
				InputStream gzipStream = new GZIPInputStream(fileStream);
				Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
				BufferedReader buffered = new BufferedReader(decoder);
				String line = null;

				while ((line = buffered.readLine()) != null) {
					if (filter1(line)) { // filter 1
						String[] parts = line.split(" ");
						if (filter2(parts[1]) && filter3(parts[1])
								&& filter4(parts[1]) && filter5(parts[1])) { // filter
																				// 2-5

							if (i == 0) {
								if (Integer.parseInt(parts[2]) > tmp_numberOfView[i]) {
									// replace the article stored in the top10
									// array
									tmp_numberOfView[i] = Integer
											.parseInt(parts[2]);
									tmp_nameOfArticle[i] = parts[1];

								}
							} else {
								if (Integer.parseInt(parts[2]) > tmp_numberOfView[i]
										&& Integer.parseInt(parts[2]) < tmp_numberOfView[i - 1]) {
									// replace the article stored in the top10
									// array
									tmp_numberOfView[i] = Integer
											.parseInt(parts[2]);
									tmp_nameOfArticle[i] = parts[1];

								}
							}

						}
					}
				}
				buffered.close();
				fileStream.close();
				gzipStream.close();
				decoder.close();
			}

			for (int i = 0; i < 10; i++) {
				numberOfView[i] = tmp_numberOfView[i];
				nameOfArticle[i] = tmp_nameOfArticle[i];
			}

		}

		/**
		 * compute the url String for the latest Wiki browsing data file
		 */

		private String getLatestURL() {
			String ret = "";
			DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd-HH"
					+ "0000");
			DateFormat dateFormat1 = new SimpleDateFormat("yyyy/yyyy-MM");
			Date date = new Date();
			ret = ("http://dumps.wikimedia.org/other/pagecounts-raw/"
					+ dateFormat1.format(date) + "/pagecounts-"
					+ dateFormat2.format(date) + ".gz");
			return ret;
		}

		// filters for the input wiki hour browsing data

		boolean filter1(String line) {
			return line.startsWith("en");
		}

		boolean filter2(String title) {
			String[] list = { "Media:", "Special:", "Talk:", "User:",
					"User_talk:", "Project:", "Project_talk:", "File:",
					"File_talk:", "MediaWiki:", "MediaWiki_talk:", "Template:",
					"Template_talk:", "Help:", "Help_talk:", "Category:",
					"Category_talk:", "Portal:", "Wikipedia:",
					"Wikipedia_talk:" };
			for (int i = 0; i < list.length; i++) {
				if (title.startsWith(list[i])) {
					return false;
				}
			}
			return true;
		}

		boolean filter3(String title) {
			return Character.isUpperCase(title.charAt(0));
		}

		boolean filter4(String title) {
			String[] list = { ".jpg", ".gif", ".png", ".JPG", ".GIF", ".PNG",
					".txt", ".ico" };
			for (int i = 0; i < list.length; i++) {
				if (title.endsWith(list[i])) {
					return false;
				}
			}
			return true;
		}

		boolean filter5(String title) {
			String[] list = { "404_error/", "Main_Page",
					"Hypertext_Transfer_Protocol", "Favicon.ico", "Search" };
			for (int i = 0; i < list.length; i++) {
				if (title.equals(list[i])) {
					return false;
				}
			}
			return true;
		}

	}

}
