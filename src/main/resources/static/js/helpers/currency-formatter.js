/**
 * Currency Formatter Helper
 * Formats numbers as currency based on the specified currency code
 */

/**
 * Format a number as currency
 * @param {number} amount - The amount to format
 * @param {string} currencyCode - The currency code (e.g., 'VND', 'USD', 'EUR')
 * @param {string} locale - Optional locale string (default: 'vi-VN' for VND, 'en-US' for others)
 * @returns {string} Formatted currency string
 */
function formatCurrency(amount, currencyCode = 'VND', locale = null) {
    // Validate input
    if (amount === null || amount === undefined || isNaN(amount)) {
        return '0';
    }

    // Parse amount to number if it's a string
    const numAmount = typeof amount === 'string' ? parseFloat(amount) : amount;

    // Determine locale based on currency if not specified
    if (!locale) {
        switch (currencyCode.toUpperCase()) {
            case 'VND':
                locale = 'vi-VN';
                break;
            case 'USD':
                locale = 'en-US';
                break;
            case 'EUR':
                locale = 'de-DE';
                break;
            case 'GBP':
                locale = 'en-GB';
                break;
            case 'JPY':
                locale = 'ja-JP';
                break;
            default:
                locale = 'en-US';
        }
    }

    try {
        // Use Intl.NumberFormat for formatting
        const formatter = new Intl.NumberFormat(locale, {
            style: 'currency',
            currency: currencyCode.toUpperCase(),
            minimumFractionDigits: currencyCode.toUpperCase() === 'VND' ? 0 : 2,
            maximumFractionDigits: currencyCode.toUpperCase() === 'VND' ? 0 : 2
        });

        return formatter.format(numAmount);
    } catch (error) {
        console.error('Error formatting currency:', error);
        // Fallback formatting
        return `${currencyCode.toUpperCase()} ${numAmount.toLocaleString()}`;
    }
}

/**
 * Format as Vietnamese Dong (VND)
 * @param {number} amount - The amount to format
 * @returns {string} Formatted VND string
 */
function formatVND(amount) {
    return formatCurrency(amount, 'VND');
}

/**
 * Format as US Dollar (USD)
 * @param {number} amount - The amount to format
 * @returns {string} Formatted USD string
 */
function formatUSD(amount) {
    return formatCurrency(amount, 'USD');
}

/**
 * Format as Euro (EUR)
 * @param {number} amount - The amount to format
 * @returns {string} Formatted EUR string
 */
function formatEUR(amount) {
    return formatCurrency(amount, 'EUR');
}

/**
 * Parse currency string back to number
 * @param {string} currencyString - The formatted currency string
 * @returns {number} Parsed number
 */
function parseCurrency(currencyString) {
    if (!currencyString) return 0;

    // Remove all non-numeric characters except decimal point and minus sign
    const cleaned = currencyString.replace(/[^\d.-]/g, '');
    return parseFloat(cleaned) || 0;
}

/**
 * Format currency with custom symbol
 * @param {number} amount - The amount to format
 * @param {string} symbol - Currency symbol (e.g., '₫', '$', '€')
 * @param {boolean} symbolAfter - Place symbol after amount (default: false)
 * @param {number} decimals - Number of decimal places (default: 0)
 * @returns {string} Formatted currency string
 */
function formatCurrencyCustom(amount, symbol = '₫', symbolAfter = true, decimals = 0) {
    if (amount === null || amount === undefined || isNaN(amount)) {
        return symbolAfter ? `0 ${symbol}` : `${symbol} 0`;
    }

    const numAmount = typeof amount === 'string' ? parseFloat(amount) : amount;
    const formattedAmount = numAmount.toLocaleString('vi-VN', {
        minimumFractionDigits: decimals,
        maximumFractionDigits: decimals
    });

    return symbolAfter ? `${formattedAmount} ${symbol}` : `${symbol} ${formattedAmount}`;
}

// Export functions for use in other modules (if using ES6 modules)
// export { formatCurrency, formatVND, formatUSD, formatEUR, parseCurrency, formatCurrencyCustom };
