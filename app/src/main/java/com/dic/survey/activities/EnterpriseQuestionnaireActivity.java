package com.dic.survey.activities;
import com.dic.survey.models.SurveyResponse;

public class EnterpriseQuestionnaireActivity extends BaseQuestionnaireActivity {
    @Override protected int getTotalSections() { return 10; }
    @Override protected String getTitle() { return "Industry / Enterprise Survey"; }
    @Override protected String getType() { return SurveyResponse.TYPE_ENTERPRISE; }

    @Override
    protected void renderSection(int s) {
        switch (s) {
            case 0: buildBasicDetails(); break;
            case 1: buildInstitutionalSupport(); break;
            case 2: buildLand(); break;
            case 3: buildInfraLogistics(); break;
            case 4: buildFinance(); break;
            case 5: buildRawMaterials(); break;
            case 6: buildLabour(); break;
            case 7: buildTechnology(); break;
            case 8: buildMarketAccess(); break;
            case 9: buildSustainability(); break;
        }
    }

    private void buildBasicDetails() {
        header("Basic Details");
        label("Name of Respondent"); textField("Full name", "respondent_name", false);
        label("Name of the Industry/Enterprise"); textField("Industry name", "industry_name", false);
        label("Category of Enterprise");
        radio("enterprise_category", new String[]{"Micro","Small","Medium","Large"});
        label("Sector");
        radio("sector", new String[]{"Primary (Agriculture/Mining)","Secondary (Manufacturing)","Tertiary (Services)"});
        label("Specific type/line of activity"); textField("e.g. Pharmaceuticals, Food Processing", "industry_type", false);
        label("Year of Establishment"); textField("e.g. 2010", "year_established", true);
        label("Total Land Area (Sq. mts)"); textField("Area in sq.mts", "land_area", true);
        label("Location Type");
        radio("location_type", new String[]{"TGIIC/Govt Industrial Area","Cluster","Private Industrial Park","Non-estate/Others"});
        label("Land Ownership");
        radio("land_ownership", new String[]{"Own land","Leased land"});
        label("Plot Utilization");
        radio("plot_utilization", new String[]{"Fully utilized","Under-utilized"});
        label("Production Model");
        radio("production_model", new String[]{"Labour-Intensive","Semi-Automated","Fully Automated"});
        label("Capacity Utilization Level");
        radio("capacity_utilization", new String[]{"Below 40%","40–60%","60–80%","80–90%","Above 90%"});
        label("Current Operational Status");
        radio("operational_status", new String[]{"Growing","Stable","Facing moderate stress","Financially stressed","Sick/restructuring"});
        label("Turnover trend over last 3 years");
        radio("turnover_trend", new String[]{"Declining >10%","Slight decline 0–10%","Stable","Moderate growth 5–15%","High growth >15%"});
        label("Annual Turnover Range");
        radio("turnover_range", new String[]{"Up to ₹50 lakh","₹50L–₹1Cr","₹1–₹3Cr","₹3–₹10Cr","₹10–₹50Cr","₹50–₹100Cr","₹100–₹500Cr","Above ₹500Cr"});
    }

    private void buildInstitutionalSupport() {
        header("Institutional Support — Single Window");
        label("Challenges in Single Window system for registration");
        checkbox("sw_delays", "Delays in departmental approvals");
        checkbox("sw_docs", "Multiple document submissions required");
        checkbox("sw_clarity", "Lack of clarity in approval guidelines");
        checkbox("sw_coord", "Limited inter-departmental coordination");
        checkbox("sw_tech", "Technical/portal issues");
        label("Which approvals cause major delays?");
        checkbox("delay_pollution", "Pollution control"); checkbox("delay_factory", "Factory license");
        checkbox("delay_power", "Power connection"); checkbox("delay_building", "Building approval");
        checkbox("delay_fire", "Fire clearance");
        label("Have you used the Industry Chasing Cell?");
        radio("chasing_cell", new String[]{"Yes, it was helpful","Yes, but issue unresolved","No/Not aware"});
        label("Biggest bottleneck with government institutions");
        radio("bottleneck", new String[]{"Approval delays","Complex procedures","Lack of coordination","Grievance redressal","Awareness/handholding"});
        label("Are you receiving DIC support in the following?");
        checkbox("dic_plots", "Support in identifying industrial plot");
        checkbox("dic_approvals", "Approvals & clearances");
        checkbox("dic_incentives", "Support for availing incentives & schemes");
        checkbox("dic_linkages", "Forward & backward linkages");
        label("Top 3 policy reforms needed to make this district more investment-friendly");
        multiField("Reform 1 | Reform 2 | Reform 3", "policy_reforms");
    }

    private void buildLand() {
        header("Land");
        label("Major challenges in expanding your industry");
        checkbox("exp_land", "Availability of land in industrial areas");
        checkbox("exp_afford", "Affordability of land in industrial areas");
        checkbox("exp_labour", "Lack of skilled labour");
        checkbox("exp_infra", "Infrastructure, Utilities & Connectivity");
        checkbox("exp_finance", "Lack of finance");
        checkbox("exp_demand", "Product demand constraints");
        label("Average cost of land per Sq. metre in your area"); textField("₹ per sq.mt", "land_cost", true);
        label("If under HILT Policy 2025 — challenges to shift");
        multiField("Describe HILT relocation challenges", "hilt_challenges");
    }

    private void buildInfraLogistics() {
        header("Industrial Infrastructure Status");
        label("Rate infrastructure available to your unit:");
        label("Internal roads"); radio("infra_internal_roads", new String[]{"Good","Not good","Not available"});
        label("External road connectivity"); radio("infra_ext_roads", new String[]{"Good","Not good","Not available"});
        label("Power supply reliability"); radio("infra_power", new String[]{"Good","Not good","Not available"});
        label("Water availability"); radio("infra_water", new String[]{"Good","Not good","Not available"});
        label("Drainage/Sewerage"); radio("infra_drainage", new String[]{"Good","Not good","Not available"});
        label("Network/Internet Connectivity"); radio("infra_network", new String[]{"Good","Not good","Not available"});
        label("CFC/CETP"); radio("infra_cfc", new String[]{"Good","Not good","Not available"});
        label("Warehousing"); radio("infra_warehousing", new String[]{"Good","Not good","Not available"});
        header("Logistics");
        label("Distance to nearest Highway (State/National)");
        radio("highway_distance", new String[]{"<2 km","2–5 km","5–10 km","10–20 km",">20 km"});
        label("Primary mode of goods transportation");
        radio("transport_mode", new String[]{"Road","Rail","Air","Multiple modes"});
        label("Distance to nearest logistics facility (railhead/ICD/airport/dry port)");
        radio("logistics_distance", new String[]{"<50 km","50–100 km",">100 km"});
        label("Logistics cost as % of total operating cost");
        radio("logistics_cost_pct", new String[]{"5–10%","10–20%","20–30%",">30%"});
        header("Power");
        label("Major power issues affecting your unit");
        checkbox("pwr_interruptions", "Frequent power interruptions");
        checkbox("pwr_voltage", "Voltage fluctuations");
        checkbox("pwr_tariff", "Power tariff & billing policy");
        checkbox("pwr_fixed_charges", "Fixed power charges during lean periods");
        checkbox("pwr_delay_conn", "Delay in new/enhanced connection");
        label("Average electricity tariff");
        radio("power_tariff", new String[]{"<₹5/unit","₹5–7/unit","₹7–9/unit","₹9–11/unit",">₹11/unit"});
        header("Water");
        label("Key water supply challenges");
        checkbox("water_qty", "Inadequate supply quantity");
        checkbox("water_irreg", "Irregular availability");
        checkbox("water_cost", "High user charges");
        checkbox("water_private", "Dependence on private water sources");
        checkbox("water_gw", "High groundwater extraction charges");
    }

    private void buildFinance() {
        header("Finance & Credit");
        label("Primary source of finance");
        radio("finance_source", new String[]{"100% Own Finance","Loan/External Finance"});
        label("If loan taken — average interest rate");
        radio("interest_rate", new String[]{"<7%","7–9%","9–11%","11–13%",">13%"});
        label("Does enterprise face working capital inconsistencies?");
        radio("working_capital_issue", new String[]{"Yes","No"});
        label("Reasons for working capital issues (if yes)");
        checkbox("wc_delayed_payment", "Delayed payments from buyers");
        checkbox("wc_loan_difficulty", "Difficulty obtaining bank loans");
        checkbox("wc_high_interest", "High interest rates");
        label("Government credit scheme availed");
        checkbox("scheme_cgtmse", "CGTMSE"); checkbox("scheme_pmegp", "PMEGP");
        checkbox("scheme_mudra", "MUDRA"); checkbox("scheme_standup", "Stand-Up India");
        checkbox("scheme_tsfc", "State Govt schemes (TSFC/Others)");
        label("Challenges in accessing finance");
        checkbox("fin_collateral", "High collateral requirements");
        checkbox("fin_high_interest", "High interest rates");
        checkbox("fin_complexity", "Complexity of eligibility/documentation");
        checkbox("fin_awareness", "Lack of awareness in remote areas");
        label("Impact of delayed incentives");
        checkbox("impact_expansion", "Postponement of expansion projects");
        checkbox("impact_investment", "Reduction in scale of investments");
        checkbox("impact_tech", "Delay in technology upgradation");
        checkbox("impact_borrowing", "Increased reliance on external borrowing");
        checkbox("impact_cashflow", "Increased financial risk and cash flow stress");
        label("Insurance coverage type");
        radio("insurance", new String[]{"No coverage","Asset insurance only","Asset + inventory","Comprehensive coverage"});
    }

    private void buildRawMaterials() {
        header("Raw Materials");
        label("Critical Raw Materials used"); textField("List critical raw materials", "critical_rm", false);
        label("% Raw Material sourced within the state"); textField("e.g. 60", "rm_instate_pct", true);
        label("% from outside the state"); textField("e.g. 30", "rm_outstate_pct", true);
        label("% imported from other countries"); textField("e.g. 10", "rm_import_pct", true);
        label("Supply disruptions in last 2 years?");
        radio("supply_disruptions", new String[]{"Yes","No"});
        label("Reasons for supply disruptions (if yes)");
        checkbox("rm_shortage", "Raw material shortage"); checkbox("rm_import_restrict", "Import restrictions/customs delays");
        checkbox("rm_price_volatile", "Price volatility"); checkbox("rm_supplier", "Supplier reliability issues");
        checkbox("rm_logistics", "High logistics costs"); checkbox("rm_bulk_lack", "Inability to procure in bulk");
        checkbox("rm_testing", "Lack of RM testing facilities (CFCs)"); checkbox("rm_quality", "Inconsistent quality/grade");
        label("Awareness of RM procurement support schemes");
        checkbox("rm_nsic", "NSIC Raw Material Assistance Scheme");
        checkbox("rm_dgft", "Duty-Free Import/Export Incentives (DGFT)");
        checkbox("rm_textile", "Textile Industry Input Subsidy");
        label("Other interventions needed for uninterrupted RM supply");
        multiField("Suggested interventions", "rm_interventions");
    }

    private void buildLabour() {
        header("Labour & Skilling");
        label("Total number of employees"); textField("Total count", "total_employees", true);
        label("Male employees"); textField("Count", "male_employees", true);
        label("Female employees"); textField("Count", "female_employees", true);
        label("Skilled employees"); textField("Count", "skilled_count", true);
        label("Semi-skilled employees"); textField("Count", "semiskilled_count", true);
        label("Unskilled employees"); textField("Count", "unskilled_count", true);
        label("Age 18–25 years %"); textField("%", "age_18_25", true);
        label("Age 26–40 years %"); textField("%", "age_26_40", true);
        label("Age 41–60 years %"); textField("%", "age_41_60", true);
        label("Primary source of workforce");
        radio("workforce_source", new String[]{"Within district","Other districts","Other states","Agent driven"});
        label("Adequate workforce for current production?");
        radio("workforce_adequate", new String[]{"Yes","No"});
        label("Reasons for inadequate workforce (if no)");
        checkbox("wf_shortage", "Shortage of workers"); checkbox("wf_high_cost", "High workforce cost");
        checkbox("wf_local", "Unavailability of local labour"); checkbox("wf_social", "Lack of social infrastructure");
        checkbox("wf_commute", "Distance/commuting difficulties");
        label("Annual employee attrition rate");
        radio("attrition", new String[]{"<5%","5–10%","10–20%","20–30%",">30%"});
        label("Key skill gaps in workforce");
        checkbox("gap_ops", "Operations and maintenance skills");
        checkbox("gap_supervisory", "Supervisory/managerial skills");
        checkbox("gap_digital", "Digital/IT skills");
        checkbox("gap_qc", "Quality control and compliance");
        checkbox("gap_soft", "Communication and soft skills");
        label("Social infrastructure gaps affecting workforce retention");
        checkbox("si_housing", "Housing"); checkbox("si_healthcare", "Healthcare");
        checkbox("si_transport", "Transport"); checkbox("si_schools", "Schools");
        checkbox("si_water", "Drinking water & sanitation");
        label("Statutory/non-statutory benefits provided");
        checkbox("ben_canteen", "On-site Canteen/Subsidized Food");
        checkbox("ben_housing", "Employee Housing/Hostel");
        checkbox("ben_transport", "Transport/Shuttle service");
        checkbox("ben_health", "Regular Health Check-ups/Insurance");
        label("Suggestions for workforce improvement");
        multiField("Suggested measures", "workforce_suggestions");
    }

    private void buildTechnology() {
        header("Technology");
        label("Technology level used in your industry");
        radio("tech_level", new String[]{"Conventional (Standard/Basic automation)","Modern (Advanced/Digital)","Highly Advanced (AI/Automation)"});
        label("Planning to adopt Industry 4.0 technology?");
        radio("industry40_plan", new String[]{"Yes","No"});
        label("Reasons for not adopting (if no)");
        checkbox("tech_no_awareness", "Lack of awareness"); checkbox("tech_no_cost", "High cost");
        checkbox("tech_no_manpower", "Lack of skilled manpower"); checkbox("tech_no_service", "Lack of service providers");
        checkbox("tech_no_maintenance", "Maintenance issues");
        label("Aware of technology upgradation schemes?");
        checkbox("aware_tidea", "T-IDEA"); checkbox("aware_yantram", "Yantram Fund");
        checkbox("aware_digitize", "MSME Digitalization Support");
        label("Does your enterprise have in-house R&D capability?");
        radio("rd_capability", new String[]{"Yes","No"});
        label("% of annual turnover spent on R&D (if yes)");
        radio("rd_pct", new String[]{"<1%","1–3%","3–5%",">5%","Not applicable"});
        label("Expected increase in production if Industry 4.0 implemented");
        radio("prod_increase_expect", new String[]{"<10%","10–25%","25–50%",">50%","Not sure"});
    }

    private void buildMarketAccess() {
        header("Market Access");
        label("Primary market for products/services");
        radio("primary_market", new String[]{"Within the State","Outside the State","Export","Both local and export"});
        label("Primary buyers of products");
        checkbox("buyer_large", "Large industries"); checkbox("buyer_govt", "Government");
        checkbox("buyer_traders", "Traders/intermediaries"); checkbox("buyer_consumers", "Direct consumers");
        label("Quality/regulatory certifications held");
        checkbox("cert_iso", "ISO"); checkbox("cert_bis", "BIS Certification");
        checkbox("cert_fssai", "FSSAI License"); checkbox("cert_gmp", "GMP");
        label("Market access challenges");
        checkbox("mkt_transport", "High transport costs"); checkbox("mkt_logistics", "Logistics issues");
        checkbox("mkt_linkages", "Limited market linkages"); checkbox("mkt_export_proc", "Export procedures");
        checkbox("mkt_road", "Poor road network"); checkbox("mkt_info", "Information gap");
        label("Do you export finished goods?");
        radio("exports", new String[]{"Yes","No"});
        label("% of production exported (if yes)");
        radio("export_pct", new String[]{"Up to 10%","10–25%","25–50%","Above 50%"});
        label("Key barriers in exporting");
        checkbox("exp_quality", "Quality standards/certification"); checkbox("exp_customs", "Customs clearance");
        checkbox("exp_logistics_cost", "Logistics cost"); checkbox("exp_info_gap", "Information gap");
        checkbox("exp_finance", "Finance"); 
        label("Awareness of GeM/ONDC portal?");
        radio("gem_aware", new String[]{"Yes","No"});
        label("Gaps in the State's industrial marketing ecosystem");
        checkbox("eco_branding", "Weak district-level branding");
        checkbox("eco_export", "Limited export facilitation");
        checkbox("eco_digital", "Inadequate digital promotion");
        checkbox("eco_events", "Low MSME participation in trade events");
    }

    private void buildSustainability() {
        header("Sustainability & Green Practices");
        label("Pollution category"); radio("pollution_cat", new String[]{"Green","Orange","Red"});
        label("Share of renewable energy in total consumption");
        radio("renewable_share", new String[]{"0%","Up to 25%","25–50%","Above 50%"});
        label("Green/sustainable practices adopted");
        checkbox("green_solar", "Renewable energy adoption (solar)");
        checkbox("green_waste", "Waste management & recycling");
        checkbox("green_water", "Water conservation & reuse");
        checkbox("green_energy_eff", "Energy-efficient machinery");
        checkbox("green_pollution", "Pollution control measures");
        label("Challenges in adopting green practices");
        checkbox("gc_cost", "High initial cost"); checkbox("gc_knowledge", "Lack of technical knowledge");
        checkbox("gc_incentives", "No/limited incentives/subsidies");
        checkbox("gc_infra", "Infrastructure constraints"); checkbox("gc_regulatory", "Regulatory complexity");
        label("Government support that would most help adopt green practices");
        checkbox("gs_subsidy", "Capital subsidy"); checkbox("gs_tech", "Technical assistance");
        checkbox("gs_training", "Training and capacity building");
        checkbox("gs_tax", "Incentives/tax benefits"); checkbox("gs_approvals", "Easier approvals");
        header("Inclusion & Government Schemes");
        label("Encouraging gender diversity and disabled workforce?");
        radio("gender_diversity", new String[]{"Yes","No"});
        label("Awareness of special schemes for women/SC/ST/minority entrepreneurs?");
        radio("special_scheme_aware", new String[]{"Fully aware","Partially aware","Not aware"});
        label("Government schemes/support availed");
        checkbox("scheme_tidea2", "T-IDEA"); checkbox("scheme_tpride", "T-PRIDE");
        checkbox("scheme_tgipass", "TG-iPass"); checkbox("scheme_task2", "TASK");
        checkbox("scheme_msme", "MSME/Central Govt schemes");
        label("Single most critical constraint affecting your business growth");
        radio("critical_constraint", new String[]{"Access to finance","Market demand","Infrastructure gaps","Skilled labour shortage","Regulatory burden"});
        label("Biggest competitive challenge");
        radio("competitive_challenge", new String[]{"Rising input costs","Competition from other states","Cheap imports","Technology gap"});
    }
}
