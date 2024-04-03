import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;

public class Douban_spider extends Test1
{
    
    Test1 test = new Test1();

    public void collect_Douban(int numOfPages)
    {
                      
        // 设置 WebDriver 的路径
        System.setProperty("webdriver.edge.driver", "H:/edgedriver_win64/msedgedriver.exe");

        // 请求头
        // options.addArguments("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        
        // 创建 WebDriver 实例
        WebDriver driver = new EdgeDriver();

        // 导航到豆瓣网站
        driver.get("https://movie.douban.com/review/best/");

        // 每一页的影评数
        int numOfCommments = 10;
        // 影评爬取
        for(int page = 0; page < numOfPages; page++) 
        {
            // 进程停顿一下
            test.stop(1000);
            // 题目列表
            List<String> titleList = new ArrayList<String>();
            // 链接列表
            List<String> herfList = new ArrayList<String>();
            // 摘要列表
            List<String> abstractList = new ArrayList<String>();
            // 时间列表
            List<String> timeList =new ArrayList<String>();
            // 作者信息列表
            List<String> authorList = new ArrayList<String>();
            for(int commments = 1;commments <= numOfCommments;commments++)
            {
                test.stop(1000);
                String cur_path = new String();
                cur_path = "//*[@id=\"content\"]/div/div[1]/div[1]/div["+ commments + "]";
                List<WebElement> elements = driver.findElements(By.xpath(cur_path));
                for(WebElement element : elements)
                {
                    // 标题
                    WebElement aelement = element.findElement(By.tagName("h2"));
                    String title = aelement.getText();
                    titleList.add(title);
                    
                    // 链接
                    String herf = aelement.findElement(By.tagName("a")).getAttribute("href");
                    herfList.add(herf);
                    
                    // 时间
                    WebElement telement = element.findElement(By.className("main-meta"));
                    timeList.add(telement.getText());
                    
                    // 作者
                    WebElement atelement = element.findElement(By.className("name"));
                    authorList.add(atelement.getText());
                    
                    // 部分评价
                    WebElement abelement = element.findElement(By.className("short-content"));
                    abstractList.add(abelement.getText());
                    
                }

            }
            
            try 
            {
                for(int i = 0; i < authorList.size(); i++)
                {
                    Thread.sleep(1000);
                    // 从列表中获取当前论文信息
                    
                    String cur_title = titleList.get(i);
                    String cur_author = authorList.get(i);
                    String cur_time = timeList.get(i);
                    String cur_hrefValue = herfList.get(i);
                    String cur_abstracts = abstractList.get(i);
                    // 总论文数+1
                    total_numberOf_papers +=1;
                    // 保存到数据库
                    test.saveToDatabase(total_numberOf_papers, cur_title, cur_author, cur_time,cur_abstracts, cur_hrefValue, 2);
                    
                    // 保存成功并输出
                    System.out.println("\ntitle:"+cur_title);  
                    System.out.format("Movie comment %d load successfully!\n", total_numberOf_papers);
                    
                }
                
            } 
            catch (Exception e) 
            {
                // TODO: handle exception
                e.printStackTrace();
            }
            try 
            {
                WebElement nextPageButton = driver.findElement(By.xpath("//*[@id=\"content\"]/div/div[1]/div[2]/span[3]/a"));
                // 使用 JavaScript 模拟点击下一页按钮
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextPageButton);
            } 
            catch (Exception e) 
            {
                // TODO: handle exception
                System.out.print("\nERROR!\nCan not found next page!\nSystem quit!\n");
                driver.quit();
            }
            // 获取"下一页"按钮的元素，进行搜索
            
            
        }
        // 5.退出浏览器
        driver.quit();
        System.out.println("\nSpider end~");
    }
    public static void main(String[] args)
    {
        Douban_spider kai = new Douban_spider();
        Scanner Scanner = new Scanner(System.in);

        System.out.print("Please input searching pages: ");
        // 要爬取的页数
        int number_Of_pages = Scanner.nextInt();
        

        // 调用爬取豆瓣方法
        kai.collect_Douban(number_Of_pages);
        Scanner.close();
    }
}
