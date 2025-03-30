import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class WebCrawler {
    private static final int MAX_DEPTH = 3;
    private static Set<String> links = new HashSet<>();
    private static Map<String, Integer> keywordCount = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. Nhập địa chỉ website ban đầu và độ sâu tìm kiếm");
        String startURL = scanner.nextLine();
        int depth = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.println("2. Nhập từ khóa tìm kiếm");
        String keyword = scanner.nextLine();

        System.out.println("3. Chương trình sẽ thực hiện lập danh sách các đường link cần crawl từ website ban đầu theo độ sâu tìm kiếm");
        crawl(startURL, depth);

        System.out.println("4. Chương trình sẽ tìm kiếm từ khóa trong danh sách các đường link và lưu các đường link có chứa từ khóa tìm kiếm, số lần xuất hiện từ khóa vào 1 file theo số thứ tự số lần xuất hiện từ khóa giảm dần.");
        searchKeyword(keyword);

        System.out.println("5. Kết thúc chương trình");
    }

    public static void crawl(String URL, int depth) {
        if (!links.contains(URL) && depth <= MAX_DEPTH) {
            try {
                links.add(URL);

                Document document = Jsoup.connect(URL).get();
                Elements linksOnPage = document.select("a[href]");

                depth++;
                for (Element page : linksOnPage) {
                    crawl(page.attr("abs:href"), depth);
                }
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }

    public static void searchKeyword(String keyword) {
        for (String link : links) {
            try {
                Document document = Jsoup.connect(link).get();
                String bodyText = document.body().text();
                int count = countOccurrences(bodyText, keyword);
                if (count > 0) {
                    keywordCount.put(link, count);
                }
            } catch (IOException e) {
                System.err.println("For '" + link + "': " + e.getMessage());
            }
        }

        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(keywordCount.entrySet());
        sortedList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        try (FileWriter writer = new FileWriter("result.txt")) {
            for (Map.Entry<String, Integer> entry : sortedList) {
                writer.write(entry.getKey() + " - " + entry.getValue() + " times\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int countOccurrences(String haystack, String needle) {
        int count = 0;
        int index = 0;
        while ((index = haystack.indexOf(needle, index)) != -1) {
            count++;
            index += needle.length();
        }
        return count;
    }
}
