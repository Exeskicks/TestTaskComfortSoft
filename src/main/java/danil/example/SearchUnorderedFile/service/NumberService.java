package danil.example.SearchUnorderedFile.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class NumberService {

    public Long findNthMinNumber(String path, int n) throws IOException {
        List<Long> numbers = readNumbersFromExcel(path);
        if (n <= 0 || n > numbers.size()) {
            throw new IllegalArgumentException("N is out of bounds");
        }
        return findKthSmallest(numbers, n - 1); // k = n-1 (индексация с 0)
    }

    private List<Long> readNumbersFromExcel(String path) throws IOException {
        List<Long> numbers = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(path);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    // Конвертируем double в BigInteger
                    double value = cell.getNumericCellValue();
                    numbers.add((long) value);
                } else if (cell != null && cell.getCellType() == CellType.STRING) {
                    // Если число записано как строка (например, "12345678901234567890")
                    try {
                        numbers.add(Long.valueOf((cell.getStringCellValue())));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return numbers;
    }

    // Алгоритм "Медиана медиан" для BigInteger
    private Long findKthSmallest(List<Long> list, int k) {
        List<Long> copy = new ArrayList<>(list);
        return select(copy, 0, copy.size() - 1, k);
    }

    private Long select(List<Long> list, int left, int right, int k) {
        while (left < right) {
            int pivotIndex = pivot(list, left, right);
            pivotIndex = partition(list, left, right, pivotIndex);
            if (k == pivotIndex) {
                return list.get(k);
            } else if (k < pivotIndex) {
                right = pivotIndex - 1;
            } else {
                left = pivotIndex + 1;
            }
        }
        return list.get(left);
    }

    // Выбор опорного элемента через медиану медиан
    private int pivot(List<Long> list, int left, int right) {
        if (right - left < 5) {
            return partition5(list, left, right);
        }
        for (int i = left; i <= right; i += 5) {
            int subRight = Math.min(i + 4, right);
            int median5 = partition5(list, i, subRight);
            swap(list, median5, left + (i - left) / 5);
        }
        int mid = (right - left) / 10 + left + 1;
        return selectIndex(list, left, left + (right - left) / 5, mid);
    }

    // Сортировка группы из ≤5 элементов и возврат медианы
    private int partition5(List<Long> list, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int j = i;
            while (j > left && list.get(j - 1).compareTo(list.get(j)) > 0) {
                swap(list, j - 1, j);
                j--;
            }
        }
        return (left + right) / 2;
    }

    // Разделение списка относительно опорного элемента
    private int partition(List<Long> list, int left, int right, int pivotIndex) {
        Long pivotValue = list.get(pivotIndex);
        swap(list, pivotIndex, right);
        int storeIndex = left;
        for (int i = left; i < right; i++) {
            if (list.get(i).compareTo(pivotValue) < 0) {
                swap(list, storeIndex, i);
                storeIndex++;
            }
        }
        swap(list, right, storeIndex);
        return storeIndex;
    }

    // Вспомогательный метод для выбора индекса (аналог select, но возвращает индекс)
    private int selectIndex(List<Long> list, int left, int right, int k) {
        while (left < right) {
            int pivotIndex = pivot(list, left, right);
            pivotIndex = partition(list, left, right, pivotIndex);
            if (k == pivotIndex) {
                return k;
            } else if (k < pivotIndex) {
                right = pivotIndex - 1;
            } else {
                left = pivotIndex + 1;
            }
        }
        return left;
    }

    private void swap(List<Long> list, int i, int j) {
        Long temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
