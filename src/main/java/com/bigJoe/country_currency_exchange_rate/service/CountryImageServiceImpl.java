package com.bigJoe.country_currency_exchange_rate.service;

import com.bigJoe.country_currency_exchange_rate.data.model.Country;
import com.bigJoe.country_currency_exchange_rate.data.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class CountryImageServiceImpl implements CountryImageService {

    @Autowired
    private CountryRepository repository;

    private static final String IMAGE_PATH = "cache/summary.png";

    @Override
    public void generateSummaryImage() {
        List<Country> allCountries = repository.findAll();

        if (allCountries.isEmpty()) return;

        int totalCountries = allCountries.size();

        List<Country> top5ByGdp = allCountries.stream()
                .filter(c -> c.getEstimated_gdp() != 0.0)
                .sorted(Comparator.comparingDouble(Country::getEstimated_gdp).reversed())
                .limit(5)
                .toList();

        LocalDateTime lastRefresh = allCountries.stream()
                .filter(c -> c.getLastRefreshedAt() != null)
                .max(Comparator.comparing(Country::getLastRefreshedAt))
                .map(Country::getLastRefreshedAt)
                .orElse(LocalDateTime.now());

        // 🖼 Create image
        int width = 600;
        int height = 400;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = image.createGraphics();

        // Background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setColor(new Color(0, 102, 204));
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        g.drawString("🌍 Country Summary", 20, 40);

        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g.setColor(Color.BLACK);
        g.drawString("total_countries: " + totalCountries, 20, 80);

        g.drawString("top_5_by_gdp:", 20, 120);

        int y = 150;
        for (Country c : top5ByGdp) {
            String line = String.format("%s - %.2f", c.getName(), c.getEstimated_gdp());
            g.drawString(line, 40, y);
            y += 30;
        }

        g.setFont(new Font("SansSerif", Font.ITALIC, 14));
        g.setColor(Color.GRAY);
        g.drawString("Last_refreshed_at: " + lastRefresh.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 20, height - 30);

        g.dispose();

        try {
            File cacheDir = new File("cache");
            if (!cacheDir.exists()) cacheDir.mkdirs();
            ImageIO.write(image, "png", new File(IMAGE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getSummaryImage() {
        File image = new File(IMAGE_PATH);
        return image.exists() ? image : null;
    }

}
