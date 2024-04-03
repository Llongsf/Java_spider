import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;

public class Zhiwang_spider extends Test1
{
    Test1 test1 = new Test1();
    public void collect_Zhiwang(int numOfPages)
    {
        String keywords = new String();
        try
        {   
            // 获取搜索关键字  
            keywords = get_Keywords();
        }
        catch(Exception e)
        {
            System.out.println("coding error!");
            System.exit(0);
        }
                  
        // 设置 WebDriver 的路径
        System.setProperty("webdriver.edge.driver", "H:/edgedriver_win64/msedgedriver.exe");

        // 请求头
        // options.addArguments("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        
        // 创建 WebDriver 实例
        WebDriver driver = new EdgeDriver();

        // 导航到论文网站
        driver.get("https://kns.cnki.net/kns8s/");

        // 设置超时时间不超过5秒
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
        
        // 获取输入框，输入selenium
        driver.findElement(By.id("txt_search")).sendKeys(keywords);

        // 获取按钮，进行搜索
        driver.findElement(By.className("search-btn")).click();

        for(int page = 0; page < numOfPages; page++) 
        {
            // 进程停顿一下
            try 
            {
                Thread.sleep(3000);
            }
            catch (Exception e) 
            {
                
                e.printStackTrace();
            }

            // 获取页面内所有论文链接<a>标签
            List<WebElement> search_aElement_Results = driver.findElements(By.className("fz14"));

            
            // 获取页面内所有论文时间信息
            List<WebElement> search_time_Results = driver.findElements(By.className("date"));
            List<String> timeList =new ArrayList<String>();
            for(WebElement searchResult : search_time_Results)
            {
                String time = searchResult.getText();
                timeList.add(time);
            }

            

            // 获取页面内所有作者信息
            List<WebElement> search_author_Results = driver.findElements(By.className("author"));
            List<String> authorList = new ArrayList<String>();

            for(WebElement authorElement: search_author_Results)
            {
                // 遍历获取作者
                String author = authorElement.getText();
                authorList.add(author);
                
            }
            // 题目列表
            List<String> titleList = new ArrayList<String>();
            // 链接列表
            List<String> herfList = new ArrayList<String>();
            // 摘要列表
            List<String> abstractList = new ArrayList<String>();
            for(WebElement aElement: search_aElement_Results)
            {
                // 获取题目
                String title = aElement.getText();
                titleList.add(title);
                
                // 获取论文链接,href属性值
                String herfValue = aElement.getAttribute("href");
                herfList.add(herfValue);

                

                String abab = new String();
                // 获取摘要
                try 
                {
                    // 进入链接页面
                    driver.get(herfValue);

                    test1.stop(1000);

                    abab = driver.findElement(By.className("row")).getText();
                } 
                catch (NoSuchElementException e) 
                {
                    System.out.println("Cannot not find abstract~");
                    abab = "NULL";
                }
                catch (TimeoutException e) 
                {
                    System.out.println("Time out when getting abstract~");
                    abab = "NULL";
                }
                
                abstractList.add(abab);

                // 页面回退
                driver.navigate().back();

                test1.stop(1000);
            }

            try 
            {
                for(int i = 0; i < authorList.size(); i++)
                {
                    // 从列表中获取当前论文信息
                    
                    String cur_title = titleList.get(i);
                    String cur_author = authorList.get(i);
                    String cur_time = timeList.get(i);
                    String cur_hrefValue = herfList.get(i);
                    String cur_abstracts = abstractList.get(i);
                    // 总论文数+1
                    total_numberOf_papers +=1;
                    // 保存到数据库
                    saveToDatabase(total_numberOf_papers, cur_title, cur_author, cur_time,cur_abstracts, cur_hrefValue, 1);
                    
                    // 保存成功并输出
                    System.out.println("\ntitle:"+cur_title);  
                    System.out.format("paper %d load successfully!\n", total_numberOf_papers);
                    
                }
                
            } 
            catch (Exception e) 
            {
                // TODO: handle exception
                e.printStackTrace();
            }
            try 
            {
                WebElement nextPageButton = driver.findElement(By.id("PageNext"));
                // 使用 JavaScript 模拟点击下一页按钮
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextPageButton);
            } 
            catch (Exception e) 
            {
                // TODO: handle exception
                System.out.print("\n\nERROR!\nCan not found next page!\nSystem quit!");
                driver.quit();
            }
            // 获取"下一页"按钮的元素，进行搜索
            
            
        }
        // 5.退出浏览器
        driver.quit();
        System.out.println("\nSpider end~");
    }
}
