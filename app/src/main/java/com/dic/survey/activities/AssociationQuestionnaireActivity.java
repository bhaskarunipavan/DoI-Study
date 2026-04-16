package com.dic.survey.activities;
import com.dic.survey.models.SurveyResponse;

public class AssociationQuestionnaireActivity extends BaseQuestionnaireActivity {
    @Override protected int getTotalSections() { return 8; }
    @Override protected String getTitle() { return "Industrial Association Survey"; }
    @Override protected String getType() { return SurveyResponse.TYPE_ASSOCIATION; }

    @Override
    protected void renderSection(int s) {
        switch (s) {
            case 0: buildBasicDetails(); break;
            case 1: buildLandInfra(); break;
            case 2: buildFinance(); break;
            case 3: buildRawMaterials(); break;
            case 4: buildManpower(); break;
            case 5: buildTechnology(); break;
            case 6: buildMarkets(); break;
            case 7: buildPolicyGovernance(); break;
        }
    }

    private void buildBasicDetails() {
        header("Basic Details");
        label("Name of the Association"); textField("Association name", "assoc_name", false);
        label("District(s) / Region / IDA Covered"); textField("Coverage area", "coverage_area", false);
        label("Year of Establishment"); textField("Year", "year_est", true);
        label("Total number of Members"); textField("Count", "member_count", true);
        header("Key Challenges for Members");
        label("What factors prevent MSMEs from scaling up?");
        checkbox("scale_credit", "Lack of access to credit with complicated compliance");
        checkbox("scale_payments", "Delayed payments from suppliers and buyers");
        checkbox("scale_manpower", "Shortage of skilled technical manpower");
        checkbox("scale_regulatory", "High regulatory burdens for approvals");
        header("Support Required by Association");
        label("Types of support required (indicate approximate %)");
        multiField("Policy advocacy _% | Regulatory handholding _% | Labour advisory _% | Market linkage _% | Technology support _%", "support_required");
    }

    private void buildLandInfra() {
        header("Land & Infrastructure Issues");
        label("Land — availability, affordability, allotment process, timelines");
        multiField("Describe land issues and proposed solutions", "land_issues");
        label("Loan/Subsidy — eligibility, process, timelines");
        multiField("Describe finance issues and proposed solutions", "loan_issues");
        label("Technology — awareness, availability, loans & subsidy, skills");
        multiField("Describe technology issues and proposed solutions", "tech_issues");
        label("Infrastructure — CFCs, roads, drainage, power, water");
        multiField("Describe infrastructure issues and proposed solutions", "infra_issues");
        label("Social Infrastructure — housing, schools, hospitals");
        multiField("Social infrastructure issues and proposed solutions", "social_infra_issues");
        label("Manpower — availability, affordability, skills");
        multiField("Manpower issues and proposed solutions", "manpower_issues");
        header("HILT Policy Barriers");
        label("Barriers to implementing HILT policy for relocating industries away from ORR");
        checkbox("hilt_incentives", "Lack of adequate relocation incentives");
        checkbox("hilt_land_cost", "High land costs in alternative zones");
        checkbox("hilt_workforce", "Reluctance of workforce to commute/relocate");
    }

    private void buildFinance() {
        header("Finance & Credit Flow");
        label("Major financial constraints (indicate approximate %)");
        multiField("Eligibility/Collateral _% | High interest _% | High compliance _% | Delay in sanction _% | Delay in subsidy _%", "financial_constraints");
        label("Immediate interventions to improve loans/subsidies access");
        multiField("Awareness campaigns, simplified applications, etc.", "finance_interventions");
        label("Preferred mechanism for receiving state industrial incentives/subsidies");
        radio("subsidy_mechanism", new String[]{"Govt subsidy reimbursement to entrepreneur","Govt subsidy release to bank","Others"});
    }

    private void buildRawMaterials() {
        header("Raw Materials & Supply Chain");
        label("Sector / industry type facing RM challenges");
        textField("Sector/industry name", "rm_sector", false);
        label("Raw material procurement challenges");
        checkbox("rmc_cost", "Raw material costs"); checkbox("rmc_bulk_finance", "Bulk purchase finance");
        checkbox("rmc_logistics", "Logistics costs"); checkbox("rmc_no_cfc", "No CFC (testing labs, storage)");
        checkbox("rmc_no_stock", "No stock available"); checkbox("rmc_limited_suppliers", "Limited local suppliers");
        checkbox("rmc_reliability", "Supplier reliability issues");
        checkbox("rmc_no_cluster", "Limited raw material clusters");
        label("Interventions to address RM challenges");
        multiField("Proposed solutions and interventions", "rm_interventions");
    }

    private void buildManpower() {
        header("Manpower & Skilling");
        label("Issues with availability of skilled labor");
        multiField("Shortage areas, qualification gaps, salary expectations", "skilled_issues");
        label("Key deficiencies in skills and qualifications");
        checkbox("def_cert", "Lack of technical certification");
        checkbox("def_practical", "Poor practical exposure");
        checkbox("def_machine", "Inadequate machine handling skills");
        checkbox("def_digital", "Lack of digital/automation skills");
        label("Primary structural cause of employee attrition");
        radio("attrition_cause", new String[]{"Wage competition from other sectors/states","Lack of social infrastructure near clusters","General reluctance of local workforce","Other"});
        label("Demographic challenges affecting labor supply");
        checkbox("demo_migration", "Youth migration to cities");
        checkbox("demo_female", "Low female workforce participation");
        checkbox("demo_wages", "High wage expectations");
        checkbox("demo_housing", "Lack of housing facilities");
        label("Interventions to improve regional skill levels");
        multiField("Strengthening ITIs, apprenticeships, industry-academia partnerships", "skill_interventions");
        header("Sector-wise Labour Shortages");
        label("Sector name and type of shortage");
        multiField("Sector | Engineers/R&D shortage | Machine operators | Unskilled labour | High attrition", "sector_labour_shortages");
    }

    private void buildTechnology() {
        header("Technology");
        label("Status of Industry 4.0 awareness/adoption among members");
        multiField("Describe adoption status across member industries", "industry40_status");
        label("Primary barrier to technology adoption");
        radio("tech_barrier", new String[]{"High capital cost","Shortage of skilled workforce","Maintenance limitations","Other"});
        label("Challenges in adopting digitization and IT integration");
        checkbox("dig_awareness", "Awareness"); checkbox("dig_costs", "Costs");
        checkbox("dig_workforce", "Availability of skilled workforce");
        label("Measures to strengthen adoption of latest technology and IT");
        multiField("Industry 4.0, ERP, digital tools — interventions needed", "tech_measures");
    }

    private void buildMarkets() {
        header("Market Access & Exports");
        label("Major market access issues currently affecting industries");
        checkbox("mkt_buyer_aware", "Buyer market awareness"); checkbox("mkt_few_buyers", "Dependence on few buyers");
        checkbox("mkt_branding", "Weak branding"); checkbox("mkt_limited_export", "Limited export market access");
        checkbox("mkt_digital", "Low digital presence");
        label("Supply chain challenges to access markets");
        checkbox("sc_transport", "Transportation delays"); checkbox("sc_logistics_cost", "High logistics cost");
        checkbox("sc_dry_port", "Dry port/hub connectivity"); checkbox("sc_last_mile", "Poor last-mile connectivity");
        label("Measures to strengthen regional market access");
        multiField("GeM onboarding, e-commerce, branding, buyer-seller meets, etc.", "market_measures");
        label("Biggest systemic barrier to entering/expanding export markets");
        radio("export_barrier", new String[]{"Cannot afford global quality certifications","High domestic logistics/port costs","Lack of export credit/market intelligence","Complex customs/documentation"});
        label("Digital platform needing most awareness/capacity building");
        radio("digital_platform", new String[]{"TS-Globalinker","GeM","ONDC","Other"});
        label("Interventions to improve market visibility of Telangana's industrial products");
        checkbox("vis_branding", "State-level branding initiatives");
        checkbox("vis_procurement", "Reserve % of govt procurement for domestic MSMEs");
        checkbox("vis_expo", "State expo events");
    }

    private void buildPolicyGovernance() {
        header("Industrial Policy, Governance & Operations");
        label("Areas requiring greater policy focus");
        checkbox("pol_green", "Green manufacturing / sustainability");
        checkbox("pol_tech_upgrade", "Technology upgradation support and subsidization");
        checkbox("pol_renewable", "Incentivizing renewable energy adoption");
        checkbox("pol_logistics", "Logistics & warehousing");
        checkbox("pol_export", "Streamlining export operations");
        checkbox("pol_regulatory", "Rationalizing regulatory requirements");
        label("Issues most affecting industries during operational phase");
        checkbox("ops_attrition", "Labour attrition");
        checkbox("ops_rm", "Inconsistent supply of raw materials");
        checkbox("ops_policy", "Frequent policy changes");
        checkbox("ops_renewals", "Frequent regulatory renewals");
        checkbox("ops_working_cap", "Depletion of working capital");
        checkbox("ops_power_water", "Inconsistent power and water supply");
        checkbox("ops_subsidies", "Release of subsidies and incentives");
        label("Explanation of most critical operational issue");
        multiField("Describe the most critical operational issue in detail", "critical_ops_explanation");
        label("Primary reason non-functional industries don't formally close");
        multiField("Pending subsidies, fear of losing licenses, other reasons", "nonfunctional_reason");
        header("Sector-Specific Challenges");
        label("Life Sciences — major challenges and interventions needed");
        multiField("API imports, R&D, manpower, environment compliance, etc.", "sector_life_sciences");
        label("IT Hardware/Electronics — major challenges and interventions");
        multiField("Innovation gaps, import dependency, R&D infrastructure", "sector_electronics");
        label("Food Processing — major challenges and interventions");
        multiField("Cold storage, quality, packaging technology, regulatory burden", "sector_food");
        label("Textile, Apparel & Leather — major challenges and interventions");
        multiField("Environmental compliance, outdated technology, cheap imports", "sector_textiles");
        label("Engineering & Capital Goods — major challenges and interventions");
        multiField("R&D, power tariffs, last mile connectivity, technology gaps", "sector_engineering");
        label("Any other sector-specific observations");
        multiField("Additional sector observations", "sector_other");
    }
}
