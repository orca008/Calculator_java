package com.example.android.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * کلاس اصلی ماشین حساب
 * این کلاس تمام منطق و رابط کاربری ماشین حساب را مدیریت می‌کند
 */
public class MainActivity extends AppCompatActivity {

    // تعریف دکمه‌های اعداد
    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    // تعریف دکمه‌های عملگرها
    private Button btnPercent, btnPlus, btnMinus, btnMultiply, btnDivision;
    // تعریف دکمه‌های ویژه
    private Button btnEqual, btnClear, btnDot, btnBracket;
    // تعریف نمایشگرهای متن
    private TextView tvInput, tvOutput;
    // متغیر برای ذخیره عبارت ریاضی
    private String process;
    // متغیر برای بررسی وضعیت پرانتزها
    private boolean checkBracket = false;

    /**
     * متد onCreate اولین متدی است که هنگام اجرای برنامه فراخوانی می‌شود
     * @param savedInstanceState وضعیت ذخیره شده برنامه
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        setupClickListeners();
    }

    /**
     * این متد تمام المان‌های رابط کاربری را به متغیرهای مربوطه متصل می‌کند
     */
    private void initializeViews() {
        // اتصال دکمه‌های اعداد
        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);

        // اتصال دکمه‌های عملگرها
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        btnDivision = findViewById(R.id.btnDivision);
        btnMultiply = findViewById(R.id.btnMultiply);
        btnEqual = findViewById(R.id.btnEqual);
        btnClear = findViewById(R.id.btnClear);
        btnDot = findViewById(R.id.btnDot);
        btnPercent = findViewById(R.id.btnPercent);
        btnBracket = findViewById(R.id.btnBracket);

        // اتصال نمایشگرهای متن
        tvInput = findViewById(R.id.tvInput);
        tvOutput = findViewById(R.id.tvOutput);
    }

    /**
     * این متد برای تمام دکمه‌ها شنونده کلیک تنظیم می‌کند
     */
    private void setupClickListeners() {
        // تنظیم شنونده برای دکمه پاک کردن
        btnClear.setOnClickListener(v -> {
            tvInput.setText("");
            tvOutput.setText("");
            checkBracket = false;
        });

        // تنظیم شنونده مشترک برای تمام دکمه‌های اعداد
        View.OnClickListener numberClickListener = v -> {
            Button button = (Button) v;
            process = tvInput.getText().toString();
            tvInput.setText(process + button.getText());
        };

        // اتصال شنونده به دکمه‌های اعداد
        btn0.setOnClickListener(numberClickListener);
        btn1.setOnClickListener(numberClickListener);
        btn2.setOnClickListener(numberClickListener);
        btn3.setOnClickListener(numberClickListener);
        btn4.setOnClickListener(numberClickListener);
        btn5.setOnClickListener(numberClickListener);
        btn6.setOnClickListener(numberClickListener);
        btn7.setOnClickListener(numberClickListener);
        btn8.setOnClickListener(numberClickListener);
        btn9.setOnClickListener(numberClickListener);

        // تنظیم شنونده برای دکمه‌های عملگرها
        btnPlus.setOnClickListener(v -> appendOperator("+"));
        btnMinus.setOnClickListener(v -> appendOperator("-"));
        btnMultiply.setOnClickListener(v -> appendOperator("×"));
        btnDivision.setOnClickListener(v -> appendOperator("÷"));
        btnDot.setOnClickListener(v -> appendOperator("."));
        btnPercent.setOnClickListener(v -> appendOperator("%"));

        // تنظیم شنونده برای دکمه پرانتز
        btnBracket.setOnClickListener(v -> {
            process = tvInput.getText().toString();
            if (checkBracket) {
                tvInput.setText(process + ")");
                checkBracket = false;
            } else {
                tvInput.setText(process + "(");
                checkBracket = true;
            }
        });

        // تنظیم شنونده برای دکمه مساوی
        btnEqual.setOnClickListener(v -> calculateResult());
    }

    /**
     * این متد عملگرها را به عبارت اضافه می‌کند
     * @param operator عملگر مورد نظر
     */
    private void appendOperator(String operator) {
        process = tvInput.getText().toString();
        if (isValidOperatorPlacement(process, operator)) {
            tvInput.setText(process + operator);
        } else {
            Toast.makeText(this, "عملگر در این موقعیت معتبر نیست", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * این متد بررسی می‌کند که آیا قرار دادن عملگر در موقعیت فعلی معتبر است
     * @param currentInput عبارت فعلی
     * @param operator عملگر مورد نظر
     * @return true اگر قرار دادن عملگر معتبر باشد
     */
    private boolean isValidOperatorPlacement(String currentInput, String operator) {
        if (currentInput.isEmpty()) {
            return operator.equals("-") || operator.equals("(");
        }
        
        char lastChar = currentInput.charAt(currentInput.length() - 1);
        if (operator.equals(".")) {
            // بررسی اعتبار نقطه اعشار
            String[] parts = currentInput.split("[+\\-×÷]");
            return !parts[parts.length - 1].contains(".");
        }
        return !isOperator(lastChar) || (operator.equals("-") && lastChar != '-');
    }

    /**
     * این متد بررسی می‌کند که آیا کاراکتر داده شده یک عملگر است
     * @param c کاراکتر مورد بررسی
     * @return true اگر کاراکتر یک عملگر باشد
     */
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '×' || c == '÷' || c == '%';
    }

    /**
     * این متد نتیجه محاسبات را محاسبه و نمایش می‌دهد
     */
    private void calculateResult() {
        process = tvInput.getText().toString();
        
        if (process.isEmpty()) {
            tvOutput.setText("0");
            return;
        }

        // بررسی پرانتزهای بسته نشده
        if (checkBracket) {
            Toast.makeText(this, "پرانتز بسته نشده است", Toast.LENGTH_SHORT).show();
            return;
        }

        // بررسی عملگرهای نامعتبر پشت سر هم
        if (process.matches(".*[+\\-×÷]{2,}.*")) {
            Toast.makeText(this, "عبارت نامعتبر است", Toast.LENGTH_SHORT).show();
            return;
        }

        // جایگزینی نمادها برای ارزیابی در JavaScript
        process = process.replaceAll("×", "*")
                        .replaceAll("%", "/100")
                        .replaceAll("÷", "/");

        Context rhino = Context.enter();
        try {
            rhino.setOptimizationLevel(-1);
            Scriptable scriptable = rhino.initStandardObjects();
            Object result = rhino.evaluateString(scriptable, process, "javascript", 1, null);
            
            // فرمت‌بندی نتیجه
            String finalResult;
            if (result instanceof Double) {
                double doubleResult = (Double) result;
                if (Double.isInfinite(doubleResult)) {
                    Toast.makeText(this, "نتیجه بسیار بزرگ است", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Double.isNaN(doubleResult)) {
                    Toast.makeText(this, "محاسبه نامعتبر است", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (doubleResult == (long) doubleResult) {
                    finalResult = String.format("%d", (long) doubleResult);
                } else {
                    finalResult = String.format("%.2f", doubleResult);
                }
            } else {
                finalResult = result.toString();
            }
            
            tvOutput.setText(finalResult);
        } catch (Exception e) {
            Toast.makeText(this, "خطا: عبارت نامعتبر است", Toast.LENGTH_SHORT).show();
        } finally {
            Context.exit();
        }
    }

    /**
     * این متد هنگام بسته شدن برنامه فراخوانی می‌شود
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // پاکسازی منابع در صورت نیاز
    }
}
