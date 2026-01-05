# Screenshots & Video Demo Guide

**Purpose**: Complete instructions for capturing visual artifacts for submission  
**Estimated Time**: 15-20 minutes

---

## Overview

You need to capture:
- **8 Screenshots** of the dashboard and checkout pages
- **1 Video Demo** (2-3 minutes) showing the complete payment flow

These visual artifacts help evaluators understand the system's functionality and validate UI requirements.

---

## Part 1: Taking Screenshots

### Tools for Screenshots

**Windows Built-in Options**:
1. **Print Screen Key** - Quick capture to clipboard
2. **Win + Shift + S** - Screenshot tool (Windows 10/11)
3. **Win + G** - Xbox Game Bar (video recording)

**Recommended Free Tools**:
- **ShareX** (Best): https://getsharex.com/ - Download, install, easy to use
- **Snagit**: Commercial but has trial
- **Greenshot**: Lightweight and free

### Step-by-Step Screenshot Capture

#### Prerequisites

1. **Start Services**:
   ```bash
   cd "c:\Users\thahe\Desktop\Payment-Gateway-with-MultiMethod-Processing-and-Hosted-Checkout"
   docker-compose up -d
   ```

2. **Wait 60 seconds** for services to fully start

3. **Verify services running**:
   ```bash
   docker-compose ps
   ```
   All 4 containers should show "Up"

4. **Create screenshots directory**:
   ```bash
   mkdir screenshots
   ```

---

### Screenshot 1: Dashboard - Login Page

**URL**: http://localhost:3000

**What to Show**:
- Login form with email field
- Password field (if present)
- Login button
- Page header/title

**How to Capture**:
1. Open browser and navigate to http://localhost:3000
2. You should see the login form
3. Take screenshot (Print Screen or tool)
4. Save as: `screenshots/01-login.png`

**Expected Elements** (with data-test-ids):
- `login-form` container
- Email input field
- Password input field
- Submit button

**Notes**:
- Capture the full login page
- Ensure text is readable
- Include the form title/heading

---

### Screenshot 2: Dashboard - Home Page (Part 1: Credentials)

**URL**: http://localhost:3000/dashboard

**Prerequisites**:
- Must be logged in (use test@example.com, any password)

**What to Show**:
- API Key display
- API Secret display
- Page heading/title

**How to Capture**:
1. After login, you're redirected to /dashboard
2. This page shows:
   - "API Key" field with value: `key_test_abc123`
   - "API Secret" field with value: `secret_test_xyz789`
3. Take screenshot showing these credentials
4. Save as: `screenshots/02-dashboard-credentials.png`

**Expected Elements** (with data-test-ids):
- `dashboard` container
- `api-key` element showing the key
- `api-secret` element showing the secret

---

### Screenshot 3: Dashboard - Home Page (Part 2: Statistics)

**Same URL**: http://localhost:3000/dashboard

**What to Show**:
- Total Transactions count
- Total Amount (in INR)
- Success Rate (percentage)

**How to Capture**:
1. Scroll down on dashboard home page if needed
2. Look for statistics section showing:
   - "Total Transactions: X"
   - "Total Amount: ₹X,XXX"
   - "Success Rate: X%"
3. Take screenshot
4. Save as: `screenshots/03-dashboard-stats.png`

**Expected Elements** (with data-test-ids):
- Statistics section/container
- `total-transactions` showing count
- `total-amount` showing amount
- `success-rate` showing percentage

**Notes**:
- Statistics may be 0 if no payments yet (that's fine)
- Just show the structure is present

---

### Screenshot 4: Dashboard - Transactions Page

**URL**: http://localhost:3000/dashboard/transactions

**What to Show**:
- Payment table header with columns
- At least one payment row (if any exist)
- Columns: Payment ID, Order ID, Amount, Method, Status, Created At

**How to Capture**:
1. Navigate to /dashboard/transactions
2. See payment table
3. If no payments yet, you can:
   - Option A: Proceed and take screenshot of empty table
   - Option B: Create a payment first (see Video Demo steps), then take screenshot
4. Take screenshot
5. Save as: `screenshots/04-transactions-table.png`

**Expected Elements** (with data-test-ids):
- `transactions-table` container
- Table headers: Payment ID, Order ID, Amount, Method, Status, Date
- Table rows with payment data

---

### Screenshot 5: Checkout Page - Order Summary

**URL**: http://localhost:3001/checkout?order_id=order_test123

**Prerequisites**:
- Need to create a real order first:
  ```bash
  curl -X POST http://localhost:8000/api/v1/orders \
    -H "X-Api-Key: key_test_abc123" \
    -H "X-Api-Secret: secret_test_xyz789" \
    -H "Content-Type: application/json" \
    -d '{"amount": 50000}'
  ```
- Copy the returned order ID

**What to Show**:
- Order amount (₹50,000)
- Order ID
- Currency (INR)
- Payment method selection tabs

**How to Capture**:
1. Navigate to: http://localhost:3001/checkout?order_id=ORDER_ID_FROM_ABOVE
2. Page should load showing order details
3. Take screenshot showing order summary
4. Save as: `screenshots/05-checkout-order-summary.png`

**Expected Elements** (with data-test-ids):
- `checkout-container` main container
- `order-summary` section
- `order-amount` showing amount
- `payment-methods-container` showing tabs

---

### Screenshot 6: Checkout - Payment Method Selection

**Same URL**: http://localhost:3001/checkout?order_id=ORDER_ID

**What to Show**:
- UPI tab/button
- Card tab/button
- Currently selected tab

**How to Capture**:
1. On the same checkout page
2. Look for payment method selector (UPI vs Card tabs)
3. Take screenshot showing both options
4. Save as: `screenshots/06-payment-methods.png`

**Expected Elements** (with data-test-ids):
- `payment-methods-container`
- `upi-tab` or similar
- `card-tab` or similar
- Active/inactive states

---

### Screenshot 7: Checkout - UPI Payment Form

**Same URL**: http://localhost:3001/checkout?order_id=ORDER_ID

**Prerequisites**:
- Must select UPI tab first

**What to Show**:
- VPA input field (e.g., "user@bank")
- Submit/Pay button
- Form heading

**How to Capture**:
1. On checkout page, click UPI tab
2. Form should appear with:
   - "VPA" input field (placeholder: "user@bank")
   - "Pay" button
3. Optionally enter a VPA: `user@okhdfcbank`
4. Take screenshot
5. Save as: `screenshots/07-upi-form.png`

**Expected Elements** (with data-test-ids):
- `upi-form` container
- `vpa-input` field
- `upi-submit-button` or similar

**Notes**:
- Don't submit yet (save that for video)
- Just show the form structure

---

### Screenshot 8: Checkout - Card Payment Form

**Same URL**: http://localhost:3001/checkout?order_id=ORDER_ID

**Prerequisites**:
- Must select Card tab

**What to Show**:
- Card Number input
- Expiry date input
- CVV input
- Holder name input
- Submit/Pay button

**How to Capture**:
1. On checkout page, click Card tab
2. Form should appear with fields:
   - "Card Number" field (placeholder: "4532...")
   - "Expiry" field (placeholder: "MM/YY")
   - "CVV" field
   - "Holder Name" field
   - "Pay" button
3. Don't enter data yet (save for video)
4. Take screenshot
5. Save as: `screenshots/08-card-form.png`

**Expected Elements** (with data-test-ids):
- `card-form` container
- `card-number-input` field
- `expiry-input` field
- `cvv-input` field
- `holder-name-input` field
- `card-submit-button` or similar

---

### Processing & Success Screenshots (Optional)

If you want to capture additional screenshots:

**Screenshot 9: Payment Processing State**
- Show loading spinner/indicator
- "Processing..." message
- Disable button

**Screenshot 10: Payment Success State**
- Show success message
- Display Payment ID
- Show confirmation details

These are captured during the video demo, so they're optional for stills.

---

## Part 2: Recording Video Demo

### Tools for Recording

**Best Options**:

1. **OBS Studio** (Recommended, Free):
   - Download: https://obsproject.com/download
   - Powerful but has learning curve
   - Supports multiple sources

2. **Windows Built-in** (Easiest):
   - Win + G (Xbox Game Bar)
   - Simple interface
   - Works for basic recordings

3. **ScreenFlow** (Mac):
   - Built-in Mac tool
   - Very easy to use

4. **Camtasia** (Paid, Trial available):
   - Professional quality
   - Good editing features

### Setup for OBS Studio (Recommended)

#### Installation
1. Download from https://obsproject.com/download
2. Install (standard installation)
3. Launch OBS Studio

#### Basic Configuration

1. **Add Scene**:
   - Click "+" under Scenes
   - Name it "Desktop"

2. **Add Source**:
   - Click "+" under Sources
   - Select "Display Capture"
   - Choose your monitor
   - Click OK

3. **Configure Output**:
   - Go to Settings → Output
   - Set Output Mode: "Simple"
   - Recording Path: `C:\Users\thahe\Desktop\Payment-Gateway-with-MultiMethod-Processing-and-Hosted-Checkout`
   - Recording Format: "mp4"
   - Video Bitrate: 8000 Kbps
   - Audio Bitrate: 128 Kbps

4. **Configure Video**:
   - Go to Settings → Video
   - Base (Canvas) Resolution: 1920x1080
   - Output (Scaled) Resolution: 1920x1080
   - Common FPS Values: 30

5. **Test**:
   - Click "Start Recording"
   - Record for 10 seconds
   - Click "Stop Recording"
   - Check that video was saved

---

### Video Demo Script (2-3 minutes)

**Total Duration**: 2-3 minutes  
**Breakdown**: 
- Opening: 10 seconds
- API order creation: 30 seconds
- Dashboard login: 30 seconds
- Checkout & payment: 60 seconds
- Dashboard verification: 30 seconds
- Closing: 10 seconds

---

#### Part 1: Opening (10 seconds)

**Narration** (Optional):
> "Welcome to the Payment Gateway demo. This video shows the complete payment flow from order creation to successful payment confirmation."

**What to Show**:
- Browser tabs or desktop
- Brief introduction

**Actions**:
- No action needed, just introduction

---

#### Part 2: API Order Creation (30 seconds)

**What to Show**:
- Create order via API
- Display order ID
- Copy order ID

**Step-by-Step**:

1. **Open Terminal**:
   - Open Command Prompt or PowerShell
   - Navigate to project directory

2. **Create Order**:
   - Run this command:
     ```bash
     curl -X POST http://localhost:8000/api/v1/orders \
       -H "X-Api-Key: key_test_abc123" \
       -H "X-Api-Secret: secret_test_xyz789" \
       -H "Content-Type: application/json" \
       -d "{\"amount\": 50000}"
     ```

3. **Show Response**:
   - Response should contain `"id": "order_xxxxxxxxxxxx"`
   - Highlight/copy this order ID

4. **Narration** (Optional):
   > "First, we create an order for ₹50,000 using the merchant API with authentication headers. The API returns an order ID that we'll use on the checkout page."

**Timing**: 30 seconds

**Screenshot**: Capture this part if you want screenshot 09

---

#### Part 3: Dashboard Login (30 seconds)

**What to Show**:
- Navigate to dashboard
- Login with test credentials
- Show API credentials
- Show statistics

**Step-by-Step**:

1. **Open New Browser Tab**:
   - Navigate to: http://localhost:3000

2. **Login Form**:
   - Wait for login page to load
   - Screenshot: Full page showing login form
   - Email field should have placeholder

3. **Enter Credentials**:
   - Email: `test@example.com`
   - Password: anything (e.g., `password`) - password field doesn't validate
   - Click "Login" button

4. **Verify Dashboard**:
   - Page redirects to /dashboard
   - Show the API Key: `key_test_abc123`
   - Show the API Secret: `secret_test_xyz789`
   - Show Statistics section

5. **Narration** (Optional):
   > "The merchant logs into the dashboard and can see their API credentials for programmatic access, along with real-time transaction statistics."

**Timing**: 30 seconds

---

#### Part 4: Checkout & Payment (60 seconds)

**What to Show**:
- Navigate to checkout page
- Show order summary
- Select payment method (UPI)
- Enter VPA
- Submit payment
- Wait for processing
- Show success screen

**Step-by-Step**:

1. **Open Checkout Page**:
   - New browser tab
   - Navigate to: `http://localhost:3001/checkout?order_id=ORDER_ID_FROM_STEP_2`
   - Replace ORDER_ID with actual ID from API response

2. **Show Order Summary**:
   - Wait for page to load
   - Screenshot: Show order details
   - Amount: ₹50,000
   - Order ID visible

3. **Select UPI Payment**:
   - Click on "UPI" tab/option
   - Screenshot: Show UPI form appears

4. **Enter VPA**:
   - Click on VPA input field
   - Type: `user@okhdfcbank`
   - Screenshot: Show form filled

5. **Submit Payment**:
   - Click "Pay" button
   - Screenshot: Show loading state

6. **Wait for Processing**:
   - Payment enters "processing" state
   - Loading spinner appears
   - Wait 5-10 seconds
   - Backend processes payment asynchronously
   - Narration: > "The payment is now being processed. Our system handles this asynchronously, allowing the checkout page to remain responsive..."

7. **Show Success**:
   - After ~5-10 seconds, page shows success
   - Screenshot: Show success page
   - Payment ID visible
   - Confirmation message

**Narration** (Optional):
> "Customer navigates to the checkout page with the order ID. They select UPI as payment method, enter their VPA (Virtual Payment Address), and submit. The system processes the payment asynchronously and returns a success confirmation with the payment ID."

**Timing**: 60 seconds (including wait time for processing)

---

#### Part 5: Dashboard Verification (30 seconds)

**What to Show**:
- Go back to dashboard
- Refresh page
- Show updated statistics
- Navigate to transactions
- Show new payment in table

**Step-by-Step**:

1. **Switch to Dashboard Tab**:
   - Click on dashboard browser tab from Part 3
   - Or navigate to: http://localhost:3000/dashboard

2. **Refresh Dashboard**:
   - Press F5 or Ctrl+R
   - Statistics should update

3. **Show Statistics Updated**:
   - Screenshot: Show updated statistics
   - Total Transactions: 1 (or more)
   - Total Amount: ₹50,000 (or updated)
   - Success Rate: 100% (or updated)

4. **Navigate to Transactions**:
   - Click on "Transactions" link/tab
   - Navigate to: http://localhost:3000/dashboard/transactions

5. **Show Payment in Table**:
   - Screenshot: Show transactions table
   - New payment row visible with:
     - Payment ID (pay_xxxx)
     - Order ID (order_xxxx)
     - Amount: 50000
     - Method: upi
     - Status: success (or completed)
     - Date/Time

6. **Narration** (Optional):
   > "Back on the merchant dashboard, we can see the transaction has been recorded. The statistics show 1 transaction for ₹50,000 with a 100% success rate. The transactions table displays the full details of the payment we just processed."

**Timing**: 30 seconds

---

#### Part 6: Closing (10 seconds)

**What to Show**:
- Brief summary
- Thank you / end screen

**Narration** (Optional):
> "This completes the demonstration of the Payment Gateway. The system successfully handles order creation, merchant authentication, checkout, payment processing, and transaction tracking—all in a production-ready, containerized environment."

**Timing**: 10 seconds

---

### Recording Instructions

#### Using Windows 10/11 Built-in (Xbox Game Bar)

**Simplest Method**:

1. Arrange windows so you can see what you want to record
2. Press **Win + G** to open Game Bar
3. Click **"Start Recording"** button (or press Win + Alt + R)
4. Perform all the actions above (Steps 1-5)
5. Press **Win + Alt + R** again to stop recording
6. Video saved to: `C:\Users\thahe\Videos\Captures`

**Advantages**:
- No installation needed
- Very simple to use
- Good quality

**Disadvantages**:
- Limited editing options
- Can't add narration easily

---

#### Using OBS Studio (Professional)

**For Best Results**:

1. **Add Audio** (Optional but recommended):
   - Settings → Audio
   - Microphone: Your microphone
   - Desktop Audio: Your speakers

2. **Start Recording**:
   - Click "Start Recording" button

3. **Perform Actions**:
   - Do all steps above
   - Speak narration if microphone is on (optional)

4. **Stop Recording**:
   - Click "Stop Recording" button
   - Video saved to output path you configured

5. **Edit (Optional)**:
   - Can use Windows Photos app or other editor
   - Trim, add title, etc.

---

### After Recording

#### Option 1: Use Raw Video (Simplest)

1. Save video as: `demo.mp4` in your project root
2. That's it!

#### Option 2: Compress Video (Recommended)

**Problem**: Video might be large (100MB+)  
**Solution**: Compress using FFmpeg

1. **Install FFmpeg**:
   - Download from: https://ffmpeg.org/download.html
   - Or via Windows Package Manager: `winget install FFmpeg`

2. **Compress Video**:
   ```bash
   cd "c:\Users\thahe\Desktop\Payment-Gateway-with-MultiMethod-Processing-and-Hosted-Checkout"
   
   ffmpeg -i input_video.mp4 -vcodec libx264 -crf 28 -acodec aac -b:a 128k demo.mp4
   ```

3. **Result**: Smaller file, still good quality

#### Option 3: Upload to External Service

If GitHub has file size limits:

1. Upload to **YouTube** (private link) or **Vimeo**
2. Share link in README.md instead of direct file

---

## Organizing and Uploading

### Directory Structure

After capturing screenshots and video:

```
Payment-Gateway-with-MultiMethod-Processing-and-Hosted-Checkout/
├── screenshots/
│   ├── 01-login.png
│   ├── 02-dashboard-credentials.png
│   ├── 03-dashboard-stats.png
│   ├── 04-transactions-table.png
│   ├── 05-checkout-order-summary.png
│   ├── 06-payment-methods.png
│   ├── 07-upi-form.png
│   └── 08-card-form.png
├── demo.mp4
└── README.md (with links added)
```

### Git Commands to Add Files

```bash
cd "c:\Users\thahe\Desktop\Payment-Gateway-with-MultiMethod-Processing-and-Hosted-Checkout"

# Add all screenshots
git add screenshots/

# Add video
git add demo.mp4

# Commit
git commit -m "Add screenshots and video demo"

# Push to GitHub
git push origin main
```

---

## Checklist

Before considering this complete:

- [ ] 8 screenshots captured (login, dashboard, transactions, checkout, upi, card, processing, success)
- [ ] Screenshots saved in `screenshots/` folder
- [ ] All screenshots are clear and legible
- [ ] Video demo recorded (2-3 minutes)
- [ ] Video shows complete flow: API → Dashboard → Checkout → Payment → Confirmation
- [ ] Video is 1920x1080 or similar resolution
- [ ] Video saved as `demo.mp4`
- [ ] Video file is under 500MB (compress if needed)
- [ ] Screenshots and video added to git
- [ ] Committed and pushed to GitHub
- [ ] README.md updated with links to media

---

## Tips for Best Results

1. **Screenshots**:
   - Use high-resolution display (1920x1080 or higher)
   - Make sure text is legible
   - Capture full page/form
   - Use PNG format for lossless quality

2. **Video**:
   - Test audio if recording with narration
   - Use steady mouse movements (not jerky)
   - Have commands ready before recording (copy/paste)
   - Keep background clean and simple
   - Record at least one complete payment flow

3. **General**:
   - Take your time - don't rush
   - Do a practice run first
   - Have all credentials/IDs ready
   - Test that services are running before starting

---

**✅ All set! Follow these instructions to capture professional-quality visual artifacts for your submission.**
