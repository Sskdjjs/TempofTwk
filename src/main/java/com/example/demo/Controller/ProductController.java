package com.example.demo.Controller;

// src/main/java/com/example/redisdemo/controller/ProductController.java
import com.example.demo.Service.Impl.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 体验缓存加速
     */
    @GetMapping("/{id}/detail")
    public String getProductDetail(@PathVariable Long id) {
        long start = System.currentTimeMillis();
        String result = productService.getProductDetail(id);
        long time = System.currentTimeMillis() - start;

        return String.format("📦 商品详情: %s<br>" +
                        "⏱️ 耗时: %d 毫秒<br>" +
                        "💡 提示: 第一次访问慢（查数据库），" +
                        "第二次就快了（读缓存）",
                result, time);
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public String search(@RequestParam String keyword) {
        long start = System.currentTimeMillis();
        String result = productService.searchProducts(keyword);
        long time = System.currentTimeMillis() - start;

        return String.format("🔍 %s<br>" +
                        "⏱️ 耗时: %d 毫秒",
                result, time);
    }

    /**
     * 记录商品浏览
     */
    @PostMapping("/{id}/view")
    public String recordView(@PathVariable Long id) {
        productService.recordProductView(id);
        Long views = productService.getProductViews(id);

        return String.format("👀 商品 %d 被浏览<br>" +
                        "总浏览量: %d<br>" +
                        "📈 Redis 实时统计，不用查数据库！",
                id, views);
    }
}
