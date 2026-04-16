package com.dic.survey.activities;
import com.dic.survey.models.SurveyResponse;

public class StakeholderQuestionnaireActivity extends BaseQuestionnaireActivity {
    @Override protected int getTotalSections() { return 6; }
    @Override protected String getTitle() { return "Stakeholder Questionnaire"; }
    @Override protected String getType() { return SurveyResponse.TYPE_STAKEHOLDER; }

    @Override
    protected void renderSection(int s) {
        switch (s) {
            case 0: buildRespondentProfile(); break;
            case 1: buildLineDepartments(); break;
            case 2: buildTGIICLand(); break;
            case 3: buildBanksFinance(); break;
            case 4: buildSkillInstitutions(); break;
            case 5: buildGeneralQuestions(); break;
        }
    }

    private void buildRespondentProfile() {
        header("Respondent Profile");
        label("Stakeholder Category");
        radio("stakeholder_category", new String[]{"Line Department","Bank/Financial Institution","Skill/Workforce Institution","Community/NGO","Other"});
        label("Name of Organization"); textField("Organization name", "org_name", false);
        label("District / Jurisdiction"); textField("District or jurisdiction", "jurisdiction", false);
        label("Level of Operation");
        radio("operation_level", new String[]{"State","Regional/Zonal","District"});
        label("Specific Department / Line of Activity"); textField("e.g. TSSPDCL, TSPCB, Labour Dept", "dept_name", false);
    }

    private void buildLineDepartments() {
        header("Commissioner of Industries");
        label("Most critical challenges affecting industrial facilitation");
        checkbox("ci_infra", "Inadequate industrial infrastructure");
        checkbox("ci_policy", "Policy inconsistency/frequent changes");
        checkbox("ci_skills", "Shortage of skilled workforce");
        checkbox("ci_credit", "Limited access to institutional credit");
        checkbox("ci_coord", "Weak inter-departmental coordination");
        label("Major administrative bottlenecks in TG-iPass approval process");
        checkbox("tgipass_delays", "Delays from line departments not adhering to timelines");
        checkbox("tgipass_tech", "Technical glitches/portal downtime");
        checkbox("tgipass_incomplete", "Incomplete applications submitted");
        checkbox("tgipass_manual", "Manual processes still in use");
        checkbox("tgipass_escalation", "Lack of auto-escalation for delayed approvals");
        label("Measures to improve single-window clearance effectiveness");
        checkbox("sw_digital", "Fully integrated digital platform");
        checkbox("sw_parallel", "Parallel processing of approvals");
        checkbox("sw_accountability", "Accountability & time-bound processing");
        label("Strategic interventions to strengthen industrial development in Telangana");
        checkbox("si_policies", "Strengthening industrial policies/guidelines");
        checkbox("si_planning", "Strengthening district-level planning and data systems");
        checkbox("si_coord", "Enhancing inter-departmental coordination");
        checkbox("si_sector", "Sector-specific policy focus (pharma, food, textiles)");
        checkbox("si_skills", "Skill ecosystem alignment with industry needs");
        checkbox("si_social_infra", "Developing social infrastructure near industrial parks");
        header("Power Distribution (TSSPDCL/TSNPDCL)");
        label("Average industrial power outage per month (hours)"); textField("Hours per month", "power_outage_hrs", true);
        label("Current industrial power tariff (₹/unit)"); textField("₹ per unit", "power_tariff_rate", true);
        label("Key challenges in power distribution to industries");
        checkbox("pwr_demand", "Fluctuating demand and load management");
        checkbox("pwr_renewable_integ", "Renewable energy integration challenges");
        checkbox("pwr_ageing", "Ageing infrastructure and maintenance issues");
        label("Support/interventions needed to improve industrial power supply");
        multiField("Describe required power supply improvements", "power_improvements");
        label("Is current power distribution adequate for upcoming industries?");
        multiField("Assessment of adequacy for future demand", "power_adequacy");
        header("Water Supply / Irrigation");
        label("Main constraints affecting industrial water availability");
        checkbox("water_source", "Limited source availability");
        checkbox("water_infra", "Infrastructure capacity limitations");
        checkbox("water_regulatory", "Regulatory/approval delays");
        checkbox("water_freshwater", "Dependence on fresh water sources only");
        label("Interventions to enhance water supply for industries");
        checkbox("water_treated", "Promotion of treated wastewater usage");
        checkbox("water_rwh", "Rainwater Harvesting (RWH)");
        checkbox("water_modernize", "Modernizing pipes and pumps");
        checkbox("water_incentives", "Incentives for water recycling");
        label("Is water distribution system ready for new/upcoming industries?");
        multiField("Assessment and gaps", "water_readiness");
        header("Pollution Control Board (TSPCB)");
        label("Challenges affecting environmental approvals and compliance");
        checkbox("pcb_zld", "Lack of ZLD systems"); checkbox("pcb_etp", "Lack of ETP");
        checkbox("pcb_staffing", "Staffing and infrastructure"); checkbox("pcb_monitoring", "Stringent monitoring requirements");
        label("Biggest compliance issue in Red/Orange category industries");
        multiField("Describe key compliance issues", "pcb_compliance_issues");
        label("Measures to promote Circular Economy");
        checkbox("ce_policy", "Active policy support and subsidies for circular economy units");
        checkbox("ce_mandates", "Sector-specific mandates (fly ash, EPR for plastics)");
        checkbox("ce_platforms", "Development of waste-trading platforms and eco-industrial parks");
        header("Labour Department");
        label("Key challenges in ensuring labour and safety compliance");
        checkbox("ld_awareness", "Low awareness of labour laws among MSMEs");
        checkbox("ld_noncompliance", "Non-compliance with approved factory building plans");
        checkbox("ld_backlog", "Backlog in inspections due to high case load");
        checkbox("ld_shortage", "Shortage of inspectors and field staff");
        checkbox("ld_informal", "Increasing informal/contract workforce");
        label("Most frequently violated labour compliance");
        radio("labour_violation", new String[]{"Non-registration under labour laws","Contract Labour Act violations","Non-payment of minimum wages","Workplace safety deficiencies","Non-maintenance of statutory registers"});
    }

    private void buildTGIICLand() {
        header("TGIIC / Land Administration");
        label("Major barriers for MSMEs in accessing/purchasing industrial plots");
        checkbox("msme_land_cost", "Exorbitant land costs");
        checkbox("msme_plot_size", "Large plot sizes — not suitable for micro/small");
        checkbox("msme_distance", "Distance from workforce/urban centres");
        checkbox("msme_finance", "Financing and collateral constraints");
        label("Main challenge in processing Land Conversion (NALA) applications");
        radio("nala_challenge", new String[]{"Discrepancies in revenue records/Dharani portal","Delays in joint inspections (Revenue + DTCP)","High conversion charges","Lack of clarity on master plan zoning"});
        label("Biggest challenge in industrial park (IALA) maintenance");
        radio("iala_challenge", new String[]{"Insufficient property tax revenue retention","Poor waste/effluent management by units","Staffing shortage","Environmental compliance & safety"});
        label("Interventions to strengthen industrial land management");
        checkbox("land_bank", "Creation of land banks with clear titles");
        checkbox("land_faster", "Faster land acquisition and conversion processes");
        checkbox("land_digital", "Integrated digital land management systems");
        checkbox("land_coord", "Strengthening coordination between TGIIC, Revenue, local bodies");
        checkbox("land_plug", "Promotion of plug-and-play industrial infrastructure");
        label("Other challenges in timely development and allotment of industrial land");
        multiField("Challenges and proposed interventions", "land_other_challenges");
        header("DTCP & HMDA");
        label("Planning-related factor most restricting industrial project approvals");
        radio("planning_restriction", new String[]{"Zoning/land-use restrictions","Delays in CLU approvals","Conversion restrictions","Proximity regulations","Road connectivity"});
        label("What planning interventions would improve industrial approvals?");
        checkbox("plan_zoning", "Dedicated industrial zoning provisions");
        checkbox("plan_simplified", "Simplified and time-bound CLU processes");
        checkbox("plan_single_window", "Single Window Portal integrating DTCP, HMDA, TGIIC");
        checkbox("plan_preapproved", "Pre-approved industrial layouts/plug-and-play zoning");
    }

    private void buildBanksFinance() {
        header("Banks & Financial Institutions");
        label("Which category of industries faces maximum difficulty in accessing credit?");
        radio("max_difficulty_credit", new String[]{"Micro enterprises","Small enterprises","Medium enterprises","Large enterprises"});
        label("Main challenges in providing credit to industries");
        checkbox("credit_collateral", "Lack of collateral (especially MSMEs)");
        checkbox("credit_cashflow", "Weak cash flows/financial records");
        checkbox("credit_regulatory", "Regulatory and compliance risks");
        checkbox("credit_subsidy", "Delays in subsidy linkage");
        checkbox("credit_risk", "Limited risk-sharing mechanisms");
        label("Average time for sanction of industrial loans");
        radio("loan_sanction_time", new String[]{"<30 days","30–60 days","60–90 days",">90 days"});
        label("Key challenges in implementing state financial schemes (including TSFC)");
        checkbox("state_scheme_delay", "Delays in subsidy release");
        checkbox("state_scheme_docs", "Complex documentation requirements");
        checkbox("state_scheme_awareness", "Low awareness among MSMEs");
        checkbox("state_scheme_coord", "Coordination gaps with departments");
        label("Average time for subsidy disbursement");
        radio("subsidy_time", new String[]{"<3 months","3–6 months","6–12 months",">12 months"});
        label("Government support that would most improve credit flow");
        radio("credit_support", new String[]{"Credit guarantee","Interest subvention","Faster subsidy release","Risk-sharing mechanisms","Improved financial literacy"});
        label("Regulatory/policy reform to protect MSMEs from sudden financial stress");
        radio("msme_protection", new String[]{"90-day tolerance period before NPA classification","Protection from NPA during market shocks","Out-of-court debt counseling/restructuring assistance"});
    }

    private void buildSkillInstitutions() {
        header("Skill & Workforce Institutions");
        label("Strategy to best bridge skill institute curriculum and industry needs");
        radio("skill_bridge", new String[]{"Collaboration between skill institutes and industry associations","Mandatory internships/apprenticeships across all technical institutions","Establishing production centres/incubators within colleges"});
        label("Primary operational bottleneck in DET/ITIs for skill development");
        radio("iti_bottleneck", new String[]{"Limited industry involvement in training","Inadequate trainer skills/infrastructure","Outdated trades with no local industrial demand","Shortage of qualified master trainers","Lack of formal MoUs with industrial estates for placements","Poor tracking of student placement post-certification"});
        label("Primary reason for local/rural youth reluctance to work in industrial zones");
        radio("youth_reluctance", new String[]{"Low wages vs urban/IT sector expectations","Migration to urban areas","Lack of residential/social infrastructure near parks","Preference for IT/ITES over manufacturing"});
        label("Interventions to strengthen industry–skill alignment");
        checkbox("skill_curriculum", "Curriculum revision aligned to industry needs");
        checkbox("skill_apprenticeship", "Apprenticeship and on-the-job training expansion");
        checkbox("skill_trainer", "Trainer upskilling programs");
        checkbox("skill_industry_training", "Incentives for industry-led training");
        checkbox("skill_placement", "Stronger placement support");
        header("Local Communities & NGOs");
        label("Key concerns of local communities regarding nearby industrial zones");
        checkbox("comm_pollution", "Pollution (air, water, noise)");
        checkbox("comm_land", "Land impacts/displacement");
        checkbox("comm_employment", "Employment opportunities");
        checkbox("comm_health", "Health & safety");
        checkbox("comm_traffic", "Traffic & infrastructure pressure");
        label("How effective are CSR initiatives of industries in your district?");
        radio("csr_effectiveness", new String[]{"Highly effective","Moderately effective","Ineffective","Non-existent/Unaware"});
        label("Interventions to improve industry–community relations");
        checkbox("ic_employment", "Local employment and skill development");
        checkbox("ic_environment", "Stronger environmental safeguards");
        checkbox("ic_grievance", "Transparent grievance redressal");
        checkbox("ic_consultation", "Regular community consultations");
        checkbox("ic_csr_aligned", "CSR aligned to local needs");
    }

    private void buildGeneralQuestions() {
        header("General Questions");
        label("Factor most constraining industrial growth in your jurisdiction");
        radio("growth_constraint", new String[]{"Infrastructure","Skills","Policy & approvals","Finance","Market access"});
        label("Rate overall ease of doing business in your jurisdiction");
        radio("ease_of_business", new String[]{"Very High","High","Moderate","Low"});
        label("Three most important reforms to accelerate industrial growth in Telangana");
        multiField("Reform 1 | Reform 2 | Reform 3 — with rationale", "top3_reforms");
        label("GST compliance challenges for industries");
        checkbox("gst_complexity", "Compliance complexity for MSMEs");
        checkbox("gst_errors", "Errors in returns and documentation");
        checkbox("gst_refunds", "Refund delays"); checkbox("gst_awareness", "Limited awareness");
        label("Primary bottleneck in registering land/property deeds for industries");
        multiField("Describe registration bottlenecks", "land_registration_bottleneck");
        label("Forest-related challenges delaying industrial projects");
        checkbox("forest_diversion", "Forest land diversion approvals");
        checkbox("forest_tree", "Tree cutting permissions");
        checkbox("forest_esz", "Eco-sensitive zone restrictions");
        checkbox("forest_central", "Central-level approval delays");
        label("Additional observations and recommendations");
        multiField("Any other observations, sector-specific issues, or recommendations", "additional_observations");
    }
}
