package com.gijun.salesmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoreCodeGenerator {

    private final JdbcTemplate jdbcTemplate;
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String generateStoreCode() {
        // 현재 가장 큰 store_code 값을 조회
        String sql = "SELECT store_code FROM stores ORDER BY store_code DESC LIMIT 1";
        String lastCode = jdbcTemplate.query(sql,
                        rs -> rs.next() ? rs.getString("store_code") : "AA-000")
                .replace("-", "");

        String alphabetPart = lastCode.substring(0, 2);
        int numberPart = Integer.parseInt(lastCode.substring(2));

        // 숫자 부분이 999를 넘어가면 알파벳을 증가
        if (numberPart >= 999) {
            alphabetPart = incrementAlphabet(alphabetPart);
            numberPart = 0;
        } else {
            numberPart++;
        }

        return String.format("%s-%03d", alphabetPart, numberPart);
    }

    private String incrementAlphabet(String current) {
        char[] chars = current.toCharArray();
        int lastIndex = chars.length - 1;

        // 마지막 문자부터 증가 시도
        while (lastIndex >= 0) {
            int index = ALPHABET.indexOf(chars[lastIndex]);
            if (index < ALPHABET.length() - 1) {
                chars[lastIndex] = ALPHABET.charAt(index + 1);
                break;
            } else {
                chars[lastIndex] = 'A';
                lastIndex--;
            }
        }

        return new String(chars);
    }
}