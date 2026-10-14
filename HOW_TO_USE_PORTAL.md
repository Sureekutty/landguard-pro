# LandGuard Pro Sureendhar — Complete Portal User Guide 🏛️
### Universal Land Due Diligence & Multi-Portal Land Verification Platform

**Official Cloud URL:** [https://sureekutty.github.io/landguard-pro/](https://sureekutty.github.io/landguard-pro/)  
**Platform Version:** 4.0 PRO (Universal Real-Name Land Engine)  
**Author & Lead Architect:** Sureendhar J  

---

## Table of Contents
1. [Platform Overview & Access Options](#1-platform-overview--access-options)
2. [Authentication & Secure Login](#2-authentication--secure-login)
3. [Step 1: Universal Land Parcel Search Engine](#3-step-1-universal-land-parcel-search-engine)
   - [Search by Any Real Owner Name](#search-by-any-real-owner-name)
   - [Search by Survey Number](#search-by-survey-number)
   - [Search by Patta Number](#search-by-patta-number)
   - [Browse All Village Parcels](#browse-all-village-parcels)
   - [Sorting & In-Results Filtering](#sorting--in-results-filtering)
4. [Step 2: Land Due Diligence & Field Intelligence](#4-step-2-land-due-diligence--field-intelligence)
5. [Statutory Title & Land Compliance Audit](#5-statutory-title--land-compliance-audit)
6. [Step 3: Strategic Price Negotiation Calculator](#6-step-3-strategic-price-negotiation-calculator)
7. [Step 4: Critical Pre-Purchase Buyer Checkpoints](#7-step-4-critical-pre-purchase-buyer-checkpoints)
8. [Step 5: Side-by-Side Parcels Comparison Matrix](#8-step-5-side-by-side-parcels-comparison-matrix)
9. [Step 6: Total Statutory Acquisition Outflow Calculator](#9-step-6-total-statutory-acquisition-outflow-calculator)
10. [Step 7: Zero-Maintenance Storage & Dossier Management](#10-step-7-zero-maintenance-storage--dossier-management)
    - [In-Website Browser Storage](#in-website-browser-storage)
    - [Backup to Device Downloads & Google Drive](#backup-to-device-downloads--google-drive)
    - [Restoring Dossiers from Backup](#restoring-dossiers-from-backup)
    - [Local MongoDB Synchronization](#local-mongodb-synchronization)
11. [Inspecting Official Government Proofs & Modals](#11-inspecting-official-government-proofs--modals)
    - [Full Due Diligence Dossier Modal](#full-due-diligence-dossier-modal)
    - [Form 1 Computerised Patta Transfer Register](#form-1-computerised-patta-transfer-register)
    - [Cadastral Survey Field Measurement Book (FMB)](#cadastral-survey-field-measurement-book-fmb)
    - [Authentic TNREGINET Form 15 Encumbrance Certificate (EC)](#authentic-tnreginet-form-15-encumbrance-certificate-ec)
    - [Registered Settlement / Sale Deed Extract](#registered-settlement--sale-deed-extract)
12. [Direct Official Government Verification Portals](#12-direct-official-government-verification-portals)

---

## 1. Platform Overview & Access Options

LandGuard Pro is a specialized real estate due diligence platform engineered for property buyers, legal advisors, and investors across Tamil Nadu. It cross-references land records against official Tamil Nadu government statutory frameworks under the **Tamil Nadu Patta Pass Book Act 1983**, the **Registration Act 1908 (Section 22-A)**, and the **Tamil Nadu Survey and Boundaries Act 1923**.

### How to Access:
- **Option A (24/7 Live Cloud Access):** Open [https://sureekutty.github.io/landguard-pro/](https://sureekutty.github.io/landguard-pro/) on any smartphone, tablet, or desktop browser. No installation or local server required.
- **Option B (Offline Standalone):** Double-click `LandGuard_Pro_v4_Standalone.html` located in `C:\Users\Asus\Projects\` to open directly in Google Chrome, Microsoft Edge, or Safari.
- **Option C (Local Enterprise Spring Boot):** Run `java -jar land-verification-platform-1.0.0.jar` and visit `http://localhost:8080`.

---

## 2. Authentication & Secure Login

When launching the platform, an encrypted access gate protects the proprietary due diligence database.

| Field | Configuration | Notes |
| :--- | :--- | :--- |
| **Username / ID** | Configured via Admin Bootstrap or User Registration | Authorized user account |
| **Password** | Configured in `.env` / BCrypt Hashed | Secure BCrypt(12) hashed credential |
| **Remember Session** | Checked by default | Keeps JWT session active in browser |
| **Guest / Read-Only Access** | Click **"⚡ 1-Click Guest Access"** | Read-only due-diligence review |

To lock the session or switch accounts, click the **"Logout / Lock"** button located on the top right navigation bar.

---

## 3. Step 1: Universal Land Parcel Search Engine

The search engine queries official Tamil Nadu government source registries (AnyTN e-Services, TNREGINET, DTCP) and pre-indexed cadastral holdings with complete data honesty:

### Search by Owner Name
1. Click the tab: **"1. Search by Any Owner Name"**.
2. Type a landholder name in the search field (e.g., `Sureendhar`, `Kandasamy`, `Senthil Kumar`, `Ramesh`, `Subramani`, `Gurusamy`) in English or Tamil.
3. Click **"Search"** or press **Enter**.
4. The system will:
   - Match existing records for that name in the verified database or connected government portal.
   - Display authentic government records with the green badge: `Official Source: AnyTN Revenue Registry | Gazette Verified`.
   - If no record exists, display: *"No matching record returned by the selected source."*
   - Only 1 explanation training sample exists for comparison, clearly tagged `DEMO DATA — NOT AN OFFICIAL RECORD`.

### Search by Survey Number
1. Click the tab: **"2. Search by Survey Number"**.
2. Enter any survey number (e.g., `142`, `143`, `146`).
3. Click **"Search"**.
4. The engine retrieves all registered sub-divisions (e.g., `142/1`, `142/2A`, `142/2B`) with their respective registered owners, road frontages, and extents.

### Search by Patta Number
1. Click the tab: **"3. Search by Patta Number"**.
2. Enter the computerised Patta passbook number (e.g., `1205`, `842`, `912`, `1342`, `1550`).
3. Click **"Search"** to pull up the registered landholder.

### Browse All Village Parcels
1. Click the tab: **"4. Browse All Parcels in Village"**.
2. The entire cadastre of the selected village (13+ diverse land holdings) is displayed simultaneously in the grid.

### Sorting & In-Results Filtering
- **Sort By Dropdown:** Sort instantly by Survey Number (Ascending), Owner Name (A to Z), Owner Name (Z to A), Land Extent (Largest First), or Land Extent (Smallest First).
- **Filter in Results Input:** Type any keyword (e.g., "Road", "Approved", "Cents", "Muthusamy") to dynamically filter the cards on screen.

---

## 4. Step 2: Land Due Diligence & Field Intelligence

Whenever a land parcel card is clicked, **Step 2** updates in real time to present ground intelligence:

- **Road Frontage:** Verifies the physical road width (e.g., 20-Foot Panchayat Main Tar Road vs. 16-Foot Layout Road).
- **Land Classification:** Identifies whether the holding is *ரயத்து புஞ்சை* (Ryotwari Dry Residential) or agricultural holding.
- **Four Boundaries (நான்கெல்லை):** Displays verified North, South, East, and West boundaries matching the Cadastral FMB ladder.
- **Property Advantages (Pros):** Direct road access, 40-year unbroken ancestral title chain, resale liquidity, and 100% constructible ground footprint.
- **Essential Buyer Cautions:** Details mandatory statutory requirements such as DTCP Section 22-A regularisation frame numbers and physical boundary stone planting by the Firka Surveyor.

---

## 5. Statutory Title & Land Compliance Audit

This desk cross-references cadastral claims against official Tamil Nadu government revenue & registration records:

| Market & Title Representation | Official Government Record Status | Statutory Audit Verdict |
| :--- | :--- | :---: |
| *"DTCP Approval Status"* | Unapproved sub-divided plot under Section 22-A of Registration Act. Future resale blocked without G.O. 78 regularisation. | <span style="color:#d97706; font-weight:bold;">REGULARISATION REQUIRED (G.O. 78)</span> |
| *"Cadastral Road Access & Boundary"* | FMB Cadastral sketch confirms 20-Foot Panchayat Main Tar Road frontage. | <span style="color:green; font-weight:bold;">GAZETTE VERIFIED</span> |
| *"30-Year Encumbrance & Title Status"* | Mortgage fully cleared & discharged via registered receipt. 40-Year EC confirms clean title. | <span style="color:green; font-weight:bold;">CLEAN TITLE</span> |
| *"Panchayat Layout Classification"* | Panchayat has zero legal authority to approve residential layouts post-2016 High Court ruling. DTCP / LPA regularisation mandatory under G.O. 78. | <span style="color:#d97706; font-weight:bold;">STATUTORY APPROVAL REQUIRED</span> |

---

## 6. Step 3: Strategic Price Negotiation Calculator

The calculator shows **"How Much Can We Negotiate?"** by calculating justifiable price deductions:

1. **Seller's Asking Price:** Calculated based on parcel extent (e.g., 1,650 Sq. Ft. @ ₹ 1,100/sq.ft = ₹ 18,15,000).
2. **Official Guideline Value:** Official registration value (e.g., 1,650 Sq. Ft. @ ₹ 750/sq.ft = ₹ 12,37,500).
3. **Legitimate Buyer Deductions:**
   - **DTCP Regularisation Outflow:** - ₹ 8,931 (Statutory fee to regularise unapproved plot under G.O. 78).
   - **Unapproved Layout Status Penalty:** - ₹ 25,000 (Market discount for unapproved sub-division).
   - **Patta Sub-division & Boundary Stones:** - ₹ 5,000 (Firka Surveyor concrete boundary stone planting).
   - **Guideline vs. Asking Margin:** - ₹ 45,000 (Negotiation room in the residential segment).
   - **Total Justified Deduction:** **- ₹ 83,931**
4. **Target Settlement Price:** Recommended closing purchase price (**₹ 17,31,069**).
5. **Opening Offer Strategy:** Start bidding at **₹ 16,81,069**, citing out-of-pocket DTCP regularisation expenses to settle at ₹ 17,31,069 (saving ₹ 83,931).

---

## 7. Step 4: Critical Pre-Purchase Buyer Checkpoints

A mandatory 6-point checklist to complete before signing the sale deed:
1. **Physical Field Stone Demarcation:** Seller must bring the Firka Surveyor to plant concrete corner boundary stones matching FMB triangulation measurements before advance payment.
2. **Patta Sub-Division Verification:** Confirm sub-division is officially carved out in Taluk office e-Services registry, not an undocumented private family partition.
3. **Hand Over Original 30-Year Parent Deeds:** Must deliver Original Partition Deed, Settlement Deed, and Original Mortgage Discharge Receipt before registry.
4. **Zero-Arrears Panchayat Kist Receipts:** Produce official Village Panchayat tax receipts up to date proving no local body tax or agricultural kist arrears.
5. **Consenting Signatures of Family Heirs:** Spouse and all adult children of registered owner must sign as consenting witnesses in the Sale Deed to prevent future partition claims.
6. **Same-Day SRO Token EC Verification:** Conduct an online EC search on the morning of registration at SRO to confirm no same-day second sale or attachment was filed.

---

## 8. Step 5: Side-by-Side Parcels Comparison Matrix

A comparative grid comparing all village land holdings:
- Survey & Sub-division
- Registered Pattadhar Name
- Land Extent (Sq. Ft.)
- Road Access Frontage
- DTCP Status (Approved vs. Unapproved)
- Statutory Regularisation Outflow
- 30-Year EC Status
- Safety Score (e.g., 98/100 Safe vs. 82/100 Negotiate)
- **Select Button:** 1-Click switches the entire dashboard to analyze that property.

---

## 9. Step 6: Total Statutory Acquisition Outflow Calculator

Calculates the complete out-of-pocket capital required to buy and register the land in Tamil Nadu:
- **Basic Land Price:** Extent (Sq. Ft.) × Negotiated Rate (₹/Sq. Ft.)
- **Stamp Duty:** **7.0%** of sale consideration value (under Article 23, Tamil Nadu Stamp Act).
- **Registration Fee:** **4.0%** of sale consideration value (under G.O. Ms. No. 58 Commercial Taxes).
- **Legal Title Search Retainer:** ₹ 8,000 (Standard 30-year advocate title clearance search).
- **Patta Sub-division & Survey Fee:** ₹ 1,200 (Treasury fee under G.O. Ms. No. 425).
- **Total Statutory Outflow:** Automatically calculated total cash required.

---

## 10. Step 7: Zero-Maintenance Storage & Dossier Management

The platform features a **Triple-Tier Storage Architecture** that eliminates database hosting bills and server maintenance:

### In-Website Browser Storage
- When you click **"Save Selected Parcel"**, the entire property dossier is stored in your device's browser storage (`localStorage`).
- It is private, permanent, and available every time you return.

### Backup to Device Downloads & Google Drive
- Click **"Backup to Device & Drive"** to export your entire portfolio.
- It automatically downloads a formatted JSON backup file (`LandGuard_Due_Diligence_Dossiers_YYYY-MM-DD.json`).
- On smartphones and Google Chrome, this file automatically syncs with your Google Drive / Downloads backup.

### Restoring Dossiers from Backup
- On any new device or phone, click **"Restore from Backup"**, select your previously exported `.json` file, and all your saved properties are restored instantly.

### Local MongoDB Synchronization
- If you run the platform locally with Spring Boot and MongoDB, clicking **"Sync Local MongoDB"** automatically syncs dossiers between your browser and MongoDB database (port 27017).

---

## 11. Inspecting Official Government Proofs & Modals

Each card features direct action buttons that open authentic government documents:

### Full Due Diligence Dossier Modal
- Click **"Inspect This Parcel (Full Popup)"** on any card.
- Features tabs for:
  1. *Patta / Chitta:* Registered owner, father's name, metric extent, tax assessment.
  2. *30-Year EC:* Complete transaction table with document numbers, executants, claimants, and satisfaction notes.
  3. *Parent Title Chain:* Chronological lineage of ownership across 40 years.
  4. *DTCP Regularisation:* Exact statutory fee breakdown (application fee, layout charge, OSR fee).

### Form 1 Computerised Patta Transfer Register
- Click the blue **"Patta"** button.
- Displays the official Tamil Nadu Revenue Department Computerised Patta Pass Book layout with digital signature citation and Tahsildar seal.
- Includes a live button to verify directly on **AnyTN Live**.

### Cadastral Survey Field Measurement Book (FMB)
- Click the green **"FMB"** button.
- Displays the official Cadastral Survey sketch with boundary measurements, road access lines, triangulation ladder, and Firka Surveyor certification.

### Authentic TNREGINET Form 15 Encumbrance Certificate (EC)
- Click the red **"Official EC"** button.
- Displays the authentic **Form 15 Certificate of Encumbrance on Property** exactly as issued by the Sub-Registrar Office (SRO).
- Shows zone, district, search period (1984–2026), document numbers, registration dates, nature of deeds, and official SRO server seal.

### Registered Settlement / Sale Deed Extract
- Click the cyan **"Deed"** button.
- Displays the verified registered deed document extract (e.g., Settlement Deed Doc No. 1120/2004) with executant, claimant, survey number, and legal conveyance text.

---

## 12. Direct Official Government Verification Portals

At the top of the dashboard, direct links take you to official Tamil Nadu government portals:
1. **e-Services AnyTN (Patta/Chitta):** `https://eservices.tn.gov.in/eservicesnew/land/chitta.html`
2. **TNREGINET (SRO Encumbrance Certificate):** `https://tnreginet.gov.in`
3. **DTCP Regularisation Portal:** `https://onlineppa.tn.gov.in`

---

## 13. Left Menu Bar & Partitioned Workspace Architecture

Version 4.0 PRO features a modern **Left Sidebar (Navigation Menu)** with clean Partitions:

### Partition 1: Cadastre & Document Desks
1. **Cadastre Explorer:** Universal search engine by Landowner Name, Survey Number, Patta Number, or Village parcel catalog.
2. **Patta / Chitta Desk:** Dedicated Form 1 revenue passbook viewer with step-by-step AnyTN retrieval procedure.
3. **30-Yr EC Desk:** Statutory Form 15 Encumbrance Certificate viewer with step-by-step TNREGINET retrieval procedure.
4. **Cadastral FMB Desk:** Field Measurement Book triangulation sketch with boundary stone coordinates and road frontage measurements.
5. **Registered Deed Desk:** Certified copy extract and 40-year parent title chain.

### Partition 2: Valuation & Storage
6. **Cost & Negotiation:** Strategic price negotiation formula and statutory acquisition outflow calculator.
7. **Saved Land Dossiers:** Triple-tier offline browser vault, JSON device backup, and Google Drive export.

### Partition 3: Government Gateways
8. **Live Portal Monitor:** Real-time uptime, HTTP response codes, latency (ms), and SSL integrity monitor for official state servers.
9. **Official Fetch Desk:** Citizen retrieval portal guide with 1-click parameter copy buttons.

---

## 14. Step-by-Step Procedures for Downloading Official Records

Every document desk in LandGuard Pro contains an interactive **Step-by-Step Retrieval Procedure Card** so citizens can obtain digitally signed government records without guesswork:

### How to Download 30-Year Encumbrance Certificate (Form 15) from TNREGINET
1. **Open TNREGINET Portal:** Launch `https://tnreginet.gov.in/` via the verified portal button.
2. **Navigate E-Services:** From the top menu, hover over **E-Services (மின்னணு சேவைகள்)** ➔ **Encumbrance Certificate (வில்லங்கச் சான்று)** ➔ Click **View EC (வில்லங்கச் சான்று விவரம் பார்க்க)**.
3. **Select Jurisdiction:** Choose Zone: **Salem**, District: **Namakkal**, Sub-Registrar Office: **Rasipuram**, Village: **Gurusamipalayam**.
4. **Enter Cadastral Identifiers:** Input Survey Number (e.g. `148`) and Subdivision (`1`). Leave subdivision blank to inspect all subdivisions in that survey number.
5. **Set Search Period:** Enter Start Date: `01/01/1984` to End Date: `Today's Date` (mandatory 30-40 year clean title search).
6. **Solve CAPTCHA & Search:** Type the 5-character visual captcha and click **Search**.
7. **Download Digitally Signed PDF:** Scroll down and click **"Download Digitally Signed Certificate"** to obtain official Form 15 with QR code.
8. **Verify in LandGuard:** Drop the downloaded PDF into LandGuard's Verification Desk to cryptographically verify digital signatures and ledger entries.

### How to Download Computerised Patta / Chitta (Form 1) from AnyTN e-Services
1. **Open AnyTN Portal:** Launch `https://eservices.tn.gov.in/` via the verified portal button.
2. **Select Service:** Click on **"View Patta & FMB / Chitta Extract"** (பட்டா & புல வரைபடம் / சிட்டா விவரங்களை பார்வையிட).
3. **Select Jurisdiction:** Select District: **Namakkal**, Taluk: **Rasipuram**, Village: **Gurusamipalayam**.
4. **Select Search Method:** Choose **Survey No** or **Patta No** (`1550`).
5. **Enter Survey Details:** Enter Survey No `148`, select Subdivision `1` from the dropdown.
6. **Enter Authentication Code:** Type the displayed CAPTCHA and click **Submit (சமர்ப்பி)**.
7. **Save & Verify:** Print or download the official Form 1 extract with digital QR seal.

---

## 15. Why Automated Data Scraping is Prohibited: Legal & Technical Realities

### Is Government Data Scraping Illegal?
**Yes.** Bulk automated scraping of government land databases is legally prohibited and carries severe civil and penal liabilities in India:

1. **Information Technology Act, 2000 (Section 43 & 66):** Extracting, copying, or downloading data from a computer system without explicit authorization from the data owner is a statutory offense. Programmatically bypassing CAPTCHAs, spoofing headers, or cracking session cookies is classified as unauthorized access.
2. **Protected Systems (Section 70 IT Act):** State data centers (NIC, CORDIS, TNREGINET) are designated as **Critical Information Infrastructure (CII)**. Unauthorized automated probing or load-intensive scraping carries imprisonment of up to **10 years**.
3. **Terms of Service:** Both AnyTN and TNREGINET explicitly bar web crawlers, spiders, screen scrapers, and automated query tools.
4. **Digital Personal Data Protection Act, 2023 (DPDP Act):** Land records contain sensitive citizen personal data (names, family lineages, registered sale prices). Harvesting this in bulk without citizen consent violates data privacy regulations.

### The Technical Barriers
- **Dynamic CAPTCHAs:** Mandated on every search request to ensure a real human is executing the query.
- **WAF & IP Blacklisting:** NIC Web Application Firewalls detect automated query patterns and ban server IP addresses (AWS, Azure, GCP, DigitalOcean) within minutes.
- **Browser CORS Restrictions:** Client-side static websites (GitHub Pages) cannot send cross-origin requests to `.gov.in` servers because government servers do not expose `Access-Control-Allow-Origin: *`.

### How LandGuard Pro Complies Legally: The Hybrid Verification Architecture
Real-world enterprise systems (banks, NBFCs, title verification advocates) operate through a **Citizen-Assisted Document Ingestion Pipeline**:
1. The citizen enters their own CAPTCHA on the official government portal to retrieve their authentic digitally signed PDF (holding legal evidentiary weight under Section 65B of the Indian Evidence Act).
2. The citizen drops that PDF into **LandGuard Pro**.
3. LandGuard computes the SHA-256 digital fingerprint, parses the text and transaction ledger, validates the digital seal, and cross-references boundaries against our master cadastre database.
4. **Result:** 100% legal, zero risk of IP bans, and rock-solid court-admissible diligence.

---

## Summary Checklist for Buyers
1. Search your target land by **Owner Name** or **Survey Number** in the **Cadastre Explorer**.
2. Inspect the **Live Government Portal Monitor** to ensure state servers are online before closing registrations.
3. Use the **Step-by-Step Procedure Cards** to retrieve official Form 15 EC and Form 1 Patta.
4. Drop your downloaded PDF into LandGuard to verify cryptographic signatures and ledger consistency.
5. Review the **Strategic Price Negotiation Calculator** to lock in out-of-pocket DTCP regularisation deductions.
6. Click **"Save Selected Parcel"** and then **"Backup to Device & Drive"** to preserve your due diligence record permanently.

