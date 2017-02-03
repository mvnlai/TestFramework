package com.testframework.scenarios;

import com.testframework.genericfunctions.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Subrogation extends GenericFunctions {

    public Boolean sp_Login(String strUsername, String strPassword) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("/html/body/div/div/div/div/div/div[2]/div/button");
        automationObject.genfunc_waitForAngular();
        /* AudaConnect Authentication
        if (strAudaConnect.equalsIgnoreCase("Yes") || strAudaConnect.equalsIgnoreCase("Y")) {
            automationObject.genfunc_Click("/html/body/div/div[2]/div/button");
            automationObject.genfunc_waitForAngular();
            
            automationObject.genfunc_SetText("//*[@id=\"CompanyCode\"]", "Company Code");
            automationObject.genfunc_SetText("//*[@id=\"UserName\"]", strUsername);
            automationObject.genfunc_SetText("//*[@id=\"Password\"]", strPassword);
      } else {
        //Valexa Authentication*/
        automationObject.genfunc_SetText("//*[@id=\"username\"]", strUsername);
        automationObject.genfunc_SetText("//*[@id=\"password\"]", strPassword);
        automationObject.genfunc_Click("/html/body/div/div[3]/form/button");
        
        //Subrogation Login
        automationObject.genfunc_waitForAngular();
        if (automationObject.genfunc_isElementPresent("/html/body/div/div/nav/div/div[2]/ul/li[5]/a/span") == false) {
            return false;
        }
        return true;
    }

    public Boolean sp_Logout() {
        automationObject.genfunc_waitForAngular();
        if (automationObject.genfunc_isElementPresent("/html/body/div/div/nav/div/div[2]/ul/li[5]/a/span")) {
            //wait for logout button to be clickable - sometimes toast notification blocks it
            automationObject.genfunc_waitForPageToLoad("/html/body/div/div/nav/div/div[2]/ul/li[5]/a/span");
            if (automationObject.genfunc_isElementPresent("//*[@id=\"toast-container\"]")) {
                automationObject.genfunc_waitForPageToLoad("/html/body/div/div/nav/div/div[2]/ul/li[5]/a/span");
            }
            automationObject.genfunc_Click("/html/body/div/div/nav/div/div[2]/ul/li[5]/a/span");
            automationObject.genfunc_waitForAngular();
            return true;
        }
        System.out.println("ERROR - Logout button is not present.");
        return false;
    }

    public Boolean sp_SelectMenuQueue(String strQueueTab, String strQueue) {

        automationObject.genfunc_waitForAngular();
        if (strQueueTab.equalsIgnoreCase("Claimant")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Claimant Queues\")]");
        }
        if (strQueueTab.equalsIgnoreCase("Defendant")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Defendant Queues\")]");
        }
        if (strQueueTab.equalsIgnoreCase("Tasks")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Tasks\")]");
        }
        if (strQueueTab.equalsIgnoreCase("Search")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Search\")]");
        }
        if (strQueueTab.equalsIgnoreCase("Netting")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Netting\")]");
        }
        if (strQueueTab.equalsIgnoreCase("Upload")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Upload\")]");
        }
        if (strQueueTab.equalsIgnoreCase("User Admin")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"User Admin\")]");
        }
        if (strQueueTab.equalsIgnoreCase("Organisation Admin")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"OrganisationAdmin\")]");
        }
        automationObject.genfunc_waitForAngular();
        if (strQueueTab.equalsIgnoreCase("Claimant") || strQueueTab.equalsIgnoreCase("Defendant")) {
            automationObject.genfunc_Click("//*[@id=\"queuesId\"]/div[contains(.,\"" + strQueue + "\")]/div[1]/div/div/div[2]/div[1]/div");
            automationObject.genfunc_waitForAngular();
        }
        return true;
    }

    private Boolean sp_SelectTablePage(Integer iPage) {
        automationObject.genfunc_waitForAngular();
        if (automationObject.genfunc_isElementSelected("//*[starts-with(@id,\"DataTables_Table\")]/span/a[" + iPage + "]") == false) {
            automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/span/a[" + iPage + "]");
        }
        return true;
    }

    public Boolean sp_SelectClaim(String strClaimantClaimNumber) {

        int iPage = 1;
        //select the dropdown to display 100 results per page
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/label/select/option[contains(.,\"100\")]");
        automationObject.genfunc_waitForAngular();
        //find the claimant claim number column
        int iCol;
        for (iCol = 1; iCol < 10; iCol++) {
            if (automationObject.genfunc_GetTextNoOutput("//*[starts-with(@id,\"DataTables_Table\")]/div[2]/div[1]/div/table/thead/tr/th[" + iCol + "]", "Claimant Claim Number") == true) {
                //Claimant claim number column has been found. Exit loop
                break;
            }
        }
        //Cycle through table pages
        while (automationObject.genfunc_isElementPresent("//*[starts-with(@id,\"DataTables_Table\")]/span/a[" + iPage + "]")) {
            sp_SelectTablePage(iPage);
            automationObject.genfunc_waitForAngular();
            //Search the column for the specific claim number
            if (automationObject.genfunc_isElementPresent("//*[starts-with(@id,\"DataTables_Table\")]/tbody/tr[contains(.,\"" + strClaimantClaimNumber + "\")]/td[" + iCol + "]") == true) {
                //Open the claim
                automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/tbody/tr[contains(.,\"" + strClaimantClaimNumber + "\")]/td[1]/i");
                return true;
            }
            iPage = iPage + 1;
        }
        System.out.println("ERROR - Could not select claim '" + strClaimantClaimNumber + "'.");
        return false;
    }

    public Boolean sp_IsClaimPresent(String strClaimantClaimNumber, String strExpectedResult) {
        int iPage = 1;
        //select the dropdown to display 100 results per page
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/label/select/option[contains(.,\"100\")]");
        automationObject.genfunc_waitForAngular();
        //find the claimant claim number column
        int iCol;
        for (iCol = 1; iCol < 10; iCol++) {
            if (automationObject.genfunc_GetTextNoOutput("//*[starts-with(@id,\"DataTables_Table\")]/div[2]/div[1]/div/table/thead/tr/th[" + iCol + "]", "Claimant Claim Number") == true) {
                //Claimant claim number column has been found. Exit loop
                break;
            }
        }
        //Cycle through table pages
        while (automationObject.genfunc_isElementPresent("//*[starts-with(@id,\"DataTables_Table\")]/span/a[" + iPage + "]")) {
            sp_SelectTablePage(iPage);
            automationObject.genfunc_waitForAngular();
            if (automationObject.genfunc_isElementPresent("//*[starts-with(@id,\"DataTables_Table\")]/tbody/tr[contains(.,\"" + strClaimantClaimNumber + "\")]/td[" + iCol + "]") == true) {
                //Output a statement when a claim has been found matching the specified claim number
                if (strExpectedResult.equalsIgnoreCase("No")) {
                    System.out.println("Failed - Claim '" + strClaimantClaimNumber + "' is present.");
                    return false;
                }
                return true;
            }
            iPage = iPage + 1;
        }
        //Output a statement when no claim has been found matching the specified claim number
        if (strExpectedResult.equalsIgnoreCase("No")) {
            return true;
        }
        System.out.println("Failed - Claim '" + strClaimantClaimNumber + "' is NOT present.");
        return false;
    }

    public Boolean sp_IsClaimNotPresent(String strClaimantClaimNumber, String strActingAs) {
        //Checks claim is not present in all queues

        ArrayList<String> alQueues = new ArrayList<>();
        if (strActingAs.equalsIgnoreCase("Claimant")) {
            //Claimant Queues
            alQueues.add("Draft Claims");
            alQueues.add("Assign Workgroup");
            alQueues.add("Assign Owner");
            alQueues.add("Rejected Claims");
            alQueues.add("Awaiting Invoice");
            alQueues.add("Draft Invoices");
            alQueues.add("Queried Invoices");
            alQueues.add("Paid Invoices");
            //Navigate to Claimant Queues
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Claimant Queues\")]");
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading");
        } else if (strActingAs.equalsIgnoreCase("Defendant")) {
            //Defendant Queues
            alQueues.add("Assign Workgroup");
            alQueues.add("Assign Owner");
            alQueues.add("Claims to be Registered");
            alQueues.add("Awaiting Acknowledgement");
            alQueues.add("Pending Quantum");
            alQueues.add("Pending Liability");
            alQueues.add("Pending Payment");
            //Navigate to Defendant Queues
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Defendant Queues\")]");
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading");
        } else {
            return false;
        }
        automationObject.genfunc_waitForAngular();
        //Cycle through queues to check the specified claim is not present
        for (String strQueue : alQueues) {
            if (automationObject.genfunc_isElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"" + strQueue + "\")]/div[1]/div/div/div[2]/div[1]/div") == true) {
                automationObject.genfunc_Click("//*[@id=\"queuesId\"]/div[contains(.,\"" + strQueue + "\")]/div[1]/div/div/div[2]/div[1]/div");
                automationObject.genfunc_waitForAngular();
                //If claim is present return false
                if (sp_IsClaimPresent(strClaimantClaimNumber, "No") == false) {
                    return false;
                }
            }
        }
        return true;
    }

    public Boolean sp_Data_CreateClaim(String strQueue, String ClaimNo, String DefendantInsurer) {

        try {
            sp_SelectMenuQueue("Claimant", "Draft Claims");
            automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div[2]/div/div/button");
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_SetText("//*[@id=\"claimantClaimNumber\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"claimantPolicyNumber\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"claimantFirstName\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"claimantLastName\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"defendantInsurer\"]", DefendantInsurer);
            automationObject.genfunc_SetText("//*[@id=\"defendantClaimNumber\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"defendantPolicyNumber\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"defendantFirstName\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"defendantLastName\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"incidentLocation\"]", "Location");
            automationObject.genfunc_SetText("//*[@id=\"incidentDescription\"]", "Damage");
            automationObject.genfunc_SetText("//*[@id=\"damageDescription\"]", "Description");
            automationObject.genfunc_SetText("//*[@id=\"dateOfIncident\"]/div/input", "01/06/2010");
            automationObject.genfunc_SetText("//*[@id=\"totalLoss\"]", "No");
            automationObject.genfunc_SetText("//*[@id=\"carUsable\"]", "Yes");

            //Saves or Submits the claim
            if (strQueue.equalsIgnoreCase("Draft Claims") == true) {
                automationObject.genfunc_Click("//button[contains(.,'Save Claim')]");
            } else {
                automationObject.genfunc_Click("//button[contains(.,'Submit Claim')]");
            }
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading");
        } catch (org.openqa.selenium.InvalidElementStateException p) {
            System.out.println("ERROR - " + ClaimNo + " failed to be created.");
            automationObject.genfunc_RefreshPage();
            automationObject.genfunc_waitForAngular();
            return false;
        } catch (org.openqa.selenium.WebDriverException q) {
            System.out.println("ERROR - " + ClaimNo + " failed to be created.");
            automationObject.genfunc_RefreshPage();
            automationObject.genfunc_waitForAngular();
            return false;
        }
        System.out.println("    " + ClaimNo + " Claim has been created.");
        return true;
    }

    public Boolean sp_IsElementPresent(String strXPath, String strValue, String strArea) {
        if (strValue.equalsIgnoreCase("No")) {
            if (automationObject.genfunc_isElementPresent(strXPath) == true) {
                System.out.println("FAILED - " + strArea + " is present.");
                return false;
            }
            return true;
        }
        if (automationObject.genfunc_isElementPresent(strXPath) == true) {
            return true;
        }
        System.out.println("FAILED - " + strArea + " is NOT present.");
        return false;
    }

    ///////////////////////////////////
    ////            Claim Action Methods
    ///////////////////////////////////
    public Boolean sp_SubmitClaim() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Submit Claim\")]");
        return true;
    }

    public Boolean sp_SaveClaim() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Save Claim\")]");
        return true;
    }

    public Boolean sp_DeleteClaim() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Delete Claim\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_waitForPageToLoad("/html/body/div[3]/div/div/div/div[3]/a[1]");
        automationObject.genfunc_Click("/html/body/div[3]/div/div/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_RejectClaim(String strRejectionReason, String strReasonNote) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Reject Claim\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"rejectionReason\"]", strRejectionReason);
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"supportingRejectionNote\"]", strReasonNote);
        automationObject.genfunc_Click("//*[@id=\"rejectClaimForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_AcknowledgeClaim() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Acknowledge Claim\")]");
        return true;
    }

    public Boolean sp_CloseClaim(String strCloseReason) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Close Claim\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"reasonToClose\"]", strCloseReason);
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//*[@id=\"closeClaimForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_ResubmitClaim(String strNote) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Resubmit Claim\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"resubmissionSupportingNote\"]", strNote);
        automationObject.genfunc_Click("//*[@id=\"resubmitClaimForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_SendToFNOL() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Send to FNOL\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//*[@id=\"sendToFNOLForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_ReturnClaimFromFNOL() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Return Claim from FNOL\")]");
        automationObject.genfunc_waitForAngular();
        //Defendant Claim Number must be provided from data setup
        automationObject.genfunc_Click("//*[@id=\"returnToHandlerFromFNOLForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_AssignWorkgroup(String strWorkgroup) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Assign Workgroup\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"workgroup\"]", strWorkgroup);
        automationObject.genfunc_Click("//*[@id=\"assignWorkgroupForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_AssignOwner(String strOwner) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Assign Owner\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"owner\"]", strOwner);
        automationObject.genfunc_Click("//*[@id=\"assignOwnerForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_Reassign(String strWorkgroup, String strOwner) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Re-assign\")]");
        automationObject.genfunc_waitForAngular();
        if (automationObject.genfunc_isElementPresent("//*[@id=\"workgroup\"]") == true) {
            automationObject.genfunc_SetText("//*[@id=\"workgroup\"]", strWorkgroup);
            automationObject.genfunc_waitForAngular();
        }
        if (automationObject.genfunc_isElementPresent("//*[@id=\"owner\"]") == true) {
            automationObject.genfunc_SetText("//*[@id=\"owner\"]", strOwner);
            automationObject.genfunc_waitForAngular();
        }
        automationObject.genfunc_Click("//*[@id=\"assignWorkgroupForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_AcceptRejection() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Accept Rejection\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_waitForPageToLoad("/html/body/div[3]/div/div/div/div[3]/a[1]");
        automationObject.genfunc_Click("/html/body/div[3]/div/div/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_AgreeQuantum() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Agree Quantum\")]");
        return true;
    }

    public Boolean sp_ConfirmPayment() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Confirm Payment\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_waitForPageToLoad("/html/body/div[3]/div/div/div/div[3]/a[1]");
        automationObject.genfunc_Click("/html/body/div[3]/div/div/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_AddInvoice(String strInvoiceRef, String strTotalNet) {
        automationObject.genfunc_waitForAngular();
        if (automationObject.genfunc_isElementPresent("//button[contains(.,\"Add Invoice\")]")) {
            automationObject.genfunc_Click("//button[contains(.,\"Add Invoice\")]");
        }
        if (automationObject.genfunc_isElementPresent("//button[contains(.,\"Add Supplementary Invoice\")]")) {
            automationObject.genfunc_Click("//button[contains(.,\"Add Supplementary Invoice\")]");
        }
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"invoiceReferenceNumber\"]", strInvoiceRef);
        automationObject.genfunc_Click("//*[@id=\"draftInvoiceForm\"]/div[2]/div[1]/div[2]/div/div/div/div[2]/div[2]/div[1]/div/span[2]/button");
        automationObject.genfunc_ClearText("//*[@id=\"totalNet\"]");
        automationObject.genfunc_SetText("//*[@id=\"totalNet\"]", strTotalNet);
        automationObject.genfunc_Click("//*[@id=\"draftInvoiceForm\"]/div[2]/div[1]/div[2]/div/div/div/div[2]/div[2]/div[1]/div/span[2]/button/i");
        automationObject.genfunc_Click("//*[@id=\"draftInvoiceForm\"]/div[1]/div/button/span");
        return true;

    }

    public Boolean sp_ReopenInvoice(String strSupportingNote) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Reopen Invoice\")]");
        automationObject.genfunc_waitForAngular();
        //Add in the provided supporting note. (Not mandatory)
        automationObject.genfunc_SetText("/*[@id=\"supportingNote\"]", strSupportingNote);
        automationObject.genfunc_waitForAngular();
        //Click on the reopen invoice button in the popup window.
        automationObject.genfunc_Click("//*[@id=\"reopenInvoiceForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_CloseInvoice(String strSupportingNote) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Close Invoice\")]");
        automationObject.genfunc_waitForAngular();
        //Add in the provided supporting note. (Not mandatory)
        automationObject.genfunc_SetText("/*[@id=\"supportingNote\"]", strSupportingNote);
        automationObject.genfunc_waitForAngular();
        //Click on the close invoice button in the popup window.
        automationObject.genfunc_Click("//*[@id=\"closeInvoiceForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_RejectInvoice(String strRejectionReason, String strReasonNote) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Reject Invoice\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"rejectionReason\"]", strRejectionReason);
        automationObject.genfunc_SetText("//*[@id=\"resubmissionSupportingNote\"]", strReasonNote);
        automationObject.genfunc_Click("//*[@id=\"rejectInvoiceForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_DeleteInvoice() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Delete Invoice\")]");
        automationObject.genfunc_waitForAngular();
        //Click yes on popup window
        automationObject.genfunc_waitForPageToLoad("/html/body/div[3]/div/div/div/div[3]/a[1]");
        automationObject.genfunc_Click("/html/body/div[3]/div/div/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_SaveInvoice() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Save Invoice\")]");
        return true;
    }

    public Boolean sp_ResubmitInvoice(String strSupportingNote) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Resubmit Invoice\")]");
        automationObject.genfunc_waitForAngular();
        //Add a default supporting note if one has not been provided.
        if (strSupportingNote.equals("")) {
            automationObject.genfunc_SetText("//*[@id=\"supportingResubmissionNote\"]", "Default automation supporting rebsubmission note.");
        }
        //Add in the provided supporting note.
        automationObject.genfunc_SetText("//*[@id=\"supportingResubmissionNote\"]", strSupportingNote);
        automationObject.genfunc_waitForAngular();
        //Click on the resubmit invoice button in the popup window.
        automationObject.genfunc_Click("//*[@id=\"resubmitInvoiceForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_SubmitInvoice() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Submit Invoice\")]");
        return true;
    }

    public Boolean sp_ReconcileInvoice() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Reconcile Invoice\")]");
        automationObject.genfunc_waitForAngular();
        //Click the reconcile button on the popup window
        automationObject.genfunc_waitForPageToLoad("/html/body/div[3]/div/div/div/div[3]/a[1]");
        automationObject.genfunc_Click("/html/body/div[3]/div/div/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_AssignLiability(String strStatus, String strClaimantLiabilityPercentage, String strDefendantLiabilityPercentage, String strSupportingNote) {
        automationObject.genfunc_waitForAngular();
        //Clicks 'Assign Initial Liability' or 'Update Liability' button (depending which is present)
        if (automationObject.genfunc_isElementPresent("//button[contains(.,\"Assign Initial Liability\")]")) {
            automationObject.genfunc_Click("//button[contains(.,\"Assign Initial Liability\")]");
        } else if (automationObject.genfunc_isElementPresent("//button[contains(.,\"Update Liability\")]")) {
            automationObject.genfunc_Click("//button[contains(.,\"Update Liability\")]");
        } else if (automationObject.genfunc_isElementPresent("//button[contains(.,\"Confirm Liability\")]")) {
            automationObject.genfunc_Click("//button[contains(.,\"Confirm Liability\")]");
        } else {
            return false;
        }
        automationObject.genfunc_waitForAngular();
        //Choose Liability Status from the dropdown
        automationObject.genfunc_Click("//*[@id=\"liabilityStatus\"]/option[contains(.,\"" + strStatus + "\")]");
        //Claimant/Defendant Liability textboxes (Optional): provided values are used.
        //Claimant/Defendant Liability textboxes (Mandatory): default values are used if none have been provided.
        //Supporting Note (Mandatory): default support note is added if one has not been provided.
        switch (strStatus) {
            case "Full Liability Accepted":
                break;
            case "Proceed Without Prejudice":
                break;
            case "Liability Unknown":
                if (!strClaimantLiabilityPercentage.equals("")) {
                    automationObject.genfunc_ClearText("//*[@id=\"claimantLiabilityPercentage\"]");
                    automationObject.genfunc_SetText("//*[@id=\"claimantLiabilityPercentage\"]", strClaimantLiabilityPercentage);
                }
                if (!strDefendantLiabilityPercentage.equals("")) {
                    automationObject.genfunc_ClearText("//*[@id=\"defendantLiabilityPercentage\"]");
                    automationObject.genfunc_SetText("//*[@id=\"defendantLiabilityPercentage\"]", strDefendantLiabilityPercentage);
                }
                break;
            case "Liability in Negotiation":
                if (!strClaimantLiabilityPercentage.equals("")) {
                    automationObject.genfunc_ClearText("//*[@id=\"claimantLiabilityPercentage\"]");
                    automationObject.genfunc_SetText("//*[@id=\"claimantLiabilityPercentage\"]", strClaimantLiabilityPercentage);
                }
                if (!strDefendantLiabilityPercentage.equals("")) {
                    automationObject.genfunc_ClearText("//*[@id=\"defendantLiabilityPercentage\"]");
                    automationObject.genfunc_SetText("//*[@id=\"defendantLiabilityPercentage\"]", strDefendantLiabilityPercentage);
                }
                if (strSupportingNote.equals("")) {
                    automationObject.genfunc_SetText("//*[@id=\"supportingLiabilityNotes\"]", "Default automation suppporting liablility note.");
                }
                break;
            case "Liability Split":
                automationObject.genfunc_ClearText("//*[@id=\"claimantLiabilityPercentage\"]");
                automationObject.genfunc_SetText("//*[@id=\"claimantLiabilityPercentage\"]", strClaimantLiabilityPercentage);
                automationObject.genfunc_ClearText("//*[@id=\"defendantLiabilityPercentage\"]");
                automationObject.genfunc_SetText("//*[@id=\"defendantLiabilityPercentage\"]", strDefendantLiabilityPercentage);
                if (strClaimantLiabilityPercentage.equals("")) {
                    automationObject.genfunc_SetText("//*[@id=\"claimantLiabilityPercentage\"]", "50");
                }
                if (strDefendantLiabilityPercentage.equals("")) {
                    automationObject.genfunc_SetText("//*[@id=\"defendantLiabilityPercentage\"]", "50");
                }
                if (strSupportingNote.equals("")) {
                    automationObject.genfunc_SetText("//*[@id=\"supportingLiabilityNotes\"]", "Suppporting Liablility Note text.");
                }
                break;
            case "Liability Repudiated":
                if (strSupportingNote.equals("")) {
                    automationObject.genfunc_SetText("//*[@id=\"supportingLiabilityNotes\"]", "Suppporting Liablility Note text.");
                }
                break;
            default:
                automationObject.genfunc_Click("//*[@id=\"liabilityStatus\"]/option[contains(.,\"Full Liability Accepted\")]");
                break;
        }
        //Add supporting liability note
        automationObject.genfunc_SetText("//*[@id=\"supportingLiabilityNotes\"]", strSupportingNote);
        //Click the Update Liability button to save the liability choices
        automationObject.genfunc_Click("//*[@id=\"setLiabilityForm\"]/div/div[3]/a[1]");
        automationObject.genfunc_waitForAngular();
        return true;
    }

    public Boolean sp_CompensatingAction(String strCompensateAction) {
        automationObject.genfunc_waitForAngular();
        //Performs action on claim before compensating action
        if (strCompensateAction.equalsIgnoreCase("Cancel Claim Acknowledgement") == true) {
            sp_AcknowledgeClaim();
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//button[contains(.,\"" + strCompensateAction + "\")]");
        }
        if (strCompensateAction.equalsIgnoreCase("Cancel Quantum Agreement") == true) {
            if (automationObject.genfunc_isElementPresent("//button[contains(.,\"Confirm Liability\")]")) {
                automationObject.genfunc_waitForAngular();
                automationObject.genfunc_Click("//button[contains(.,\"" + strCompensateAction + "\")]");
            } else {
                sp_AgreeQuantum();
                automationObject.genfunc_waitForAngular();
                automationObject.genfunc_Click("//button[contains(.,\"" + strCompensateAction + "\")]");
            }
        }
        if (strCompensateAction.equalsIgnoreCase("Cancel Payment") == true) {
            sp_ConfirmPayment();
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//button[contains(.,\"" + strCompensateAction + "\")]");
        }
        if (strCompensateAction.equalsIgnoreCase("Cancel Reconciliation") == true) {
            sp_ReconcileInvoice();
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//button[contains(.,\"" + strCompensateAction + "\")]");
        }
        automationObject.genfunc_waitForAngular();
        //Click the reconcile button on the popup window
        automationObject.genfunc_Click("//*[@id=\"cancelActionForm\"]/div/div[3]/a[1]");

        return true;
    }

    public Boolean sp_SubmitNewDraft(String ClaimNo, String DefendantInsurer) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div[2]/div/div/button");
        automationObject.genfunc_waitForAngular();
        sp_PopulateNewDraft(ClaimNo, DefendantInsurer, "10/08/2016", "Yes", "Yes");
        sp_SubmitClaim();

        return true;
    }

    public Boolean sp_PopulateNewDraft(String ClaimNo, String DefendantInsurer, String DateOfIncident, String TotalLoss, String CarUsable) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"claimantClaimNumber\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"claimantPolicyNumber\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"claimantFirstName\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"claimantLastName\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"defendantInsurer\"]", DefendantInsurer);
        automationObject.genfunc_SetText("//*[@id=\"defendantClaimNumber\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"defendantPolicyNumber\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"defendantFirstName\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"defendantLastName\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"incidentLocation\"]", "Location");
        automationObject.genfunc_SetText("//*[@id=\"incidentDescription\"]", "Damage");
        automationObject.genfunc_SetText("//*[@id=\"damageDescription\"]", "Description");
        automationObject.genfunc_SetText("//*[@id=\"dateOfIncident\"]/div/input", DateOfIncident);
        automationObject.genfunc_SetText("//*[@id=\"totalLoss\"]", TotalLoss);
        automationObject.genfunc_SetText("//*[@id=\"carUsable\"]", CarUsable);
        return true;
    }

    public Boolean sp_Action(String strAction, String strInput1, String strInput2, String strInput3, String strInput4) {
        if (strAction.equalsIgnoreCase("Submit Claim")) {
            return sp_SubmitClaim();
        }
        if (strAction.equalsIgnoreCase("Save Claim")) {
            return sp_SaveClaim();
        }
        if (strAction.equalsIgnoreCase("Delete Claim")) {
            return sp_DeleteClaim();
        }
        if (strAction.equalsIgnoreCase("Reject Claim")) {
            return sp_RejectClaim(strInput1, strInput2);
        }
        if (strAction.equalsIgnoreCase("Acknowledge Claim")) {
            return sp_AcknowledgeClaim();
        }
        if (strAction.equalsIgnoreCase("Close Claim")) {
            return sp_CloseClaim(strInput1);
        }
        if (strAction.equalsIgnoreCase("Resubmit Claim")) {
            return sp_ResubmitClaim(strInput1);
        }
        if (strAction.equalsIgnoreCase("Send To FNOL")) {
            return sp_SendToFNOL();
        }
        if (strAction.equalsIgnoreCase("Return Claim From FNOL")) {
            return sp_ReturnClaimFromFNOL();
        }
        if (strAction.equalsIgnoreCase("Assign Workgroup")) {
            return sp_AssignWorkgroup(strInput1);
        }
        if (strAction.equalsIgnoreCase("Assign Owner")) {
            return sp_AssignOwner(strInput1);
        }
        if (strAction.equalsIgnoreCase("Re-assign")) {
            return sp_Reassign(strInput1, strInput2);
        }
        if (strAction.equalsIgnoreCase("Accept Rejection")) {
            return sp_AcceptRejection();
        }
        if (strAction.equalsIgnoreCase("Agree Quantum")) {
            return sp_AgreeQuantum();
        }
        if (strAction.equalsIgnoreCase("Confirm Payment")) {
            return sp_ConfirmPayment();
        }
        if (strAction.equalsIgnoreCase("Add Invoice") || strAction.equalsIgnoreCase("Add Supplementary Invoice")) {
            return sp_AddInvoice(strInput1, strInput2);
        }
        if (strAction.equalsIgnoreCase("Reopen Invoice")) {
            return sp_ReopenInvoice(strInput1);
        }
        if (strAction.equalsIgnoreCase("Close Invoice")) {
            return sp_CloseInvoice(strInput1);
        }
        if (strAction.equalsIgnoreCase("Reject Invoice")) {
            return sp_RejectInvoice(strInput1, strInput2);
        }
        if (strAction.equalsIgnoreCase("Delete Invoice")) {
            return sp_DeleteInvoice();
        }
        if (strAction.equalsIgnoreCase("Save Invoice")) {
            return sp_SaveInvoice();
        }
        if (strAction.equalsIgnoreCase("Resubmit Invoice")) {
            return sp_ResubmitInvoice(strInput1);
        }
        if (strAction.equalsIgnoreCase("Submit Invoice")) {
            return sp_SubmitInvoice();
        }
        if (strAction.equalsIgnoreCase("Reconcile Invoice")) {
            return sp_ReconcileInvoice();
        }
        if (strAction.equalsIgnoreCase("Assign Initial Liability") || strAction.equalsIgnoreCase("Update Liability") || strAction.equalsIgnoreCase("Confirm Liability")) {
            return sp_AssignLiability(strInput1, strInput2, strInput3, strInput4);
        }
        if (strAction.equalsIgnoreCase("Cancel Claim Acknowledgement") || strAction.equalsIgnoreCase("Cancel Quantum Agreement") || strAction.equalsIgnoreCase("Cancel Payment") || strAction.equalsIgnoreCase("Cancel Reconciliation")) {
            return sp_CompensatingAction(strAction);
        }

        return true;
    }

    public Boolean sp_PerformAction(String strClaimantClaimNumber, String strAction, String strActionInput1, String strActionInput2,
            String strActionInput3, String strActionInput4, String strActionPanelText, String strEvent, String strScreenshotLocation) {
        System.out.println("    Performing '" + strAction + "' action for claim '" + strClaimantClaimNumber + "'.");

        try {

            //Do Action
            sp_Action(strAction, strActionInput1, strActionInput2, strActionInput3, strActionInput4);
            automationObject.genfunc_waitForAngular();

    //Commented out for Sprint 15 Screenshot Functionality
            /*Check Action Panel Text
            if (strActionPanelText.equalsIgnoreCase("None")) {
                //No expected action panel text
            } else {
                if (automationObject.genfunc_GetTextNoOutput("//*[@id=\"tabs-page-content-area\"]//data-action-panel/div/div/p[1]", strActionPanelText) == false) {
                    automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimantClaimNumber + " - " + strAction + " - ActionPanelText.png");
                    System.out.println("Failed - Action panel text is not as expected. See screenshot for more details.");
                }
            }

            //Check Event
            automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div/data-event-log/div/div[1]/h3"); //Expand Event Log panel
            automationObject.genfunc_waitForAngular();
            /Check the Event of the first row
            if (automationObject.genfunc_GetTextNoOutput(".//*[@id='eventLogTableId']//table/tbody/tr[1]/td[3]", strEvent) == false) {
                automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimantClaimNumber + " - " + strAction + " - Event.png");
                System.out.println("Failed - Event text is not as expected. See screenshot for more details.");
            }

            //Tab back to original page
            automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading");
            automationObject.genfunc_waitForAngular();
            */
        } //Catches the exception if the action fails
        catch (Exception e) {
            System.out.println("ERROR - '" + strAction + "' action on claim '" + strClaimantClaimNumber + "' failed to be correctly performed.");
            automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimantClaimNumber + " - " + strAction + ".png");
            automationObject.genfunc_RefreshPage();
            automationObject.genfunc_waitForAngular();
            sp_Logout();
            return false;
        }
        return true;
    }

    /////////////////////////////////////////////
    ////            Subrogation Workflow Methods
    /////////////////////////////////////////////
    public Boolean sp_Workflow_DataSetRunner(String DataSetName) {

        if (DataSetName.equalsIgnoreCase("")) {
            return false;
        }

        //To find the last row in the data set
        int iLastRow = 0;
        while (getDataSet("$" + DataSetName + "(" + iLastRow + ",1)").equalsIgnoreCase("") == false) {
            iLastRow++;
        }

        for (int iCurrentRow = 0; iCurrentRow < iLastRow; iCurrentRow++) {

            //Get values from data set
            String strActingAs = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",1)");
            String strUsername = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",2)");
            String strPassword = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",3)");
            String strCurrentQueue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",4)");
            String strClaimantClaimNumber = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",5)");
            String strAction = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",6)");
            String strActionInput1 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",7)");
            String strActionInput2 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",8)");
            String strActionInput3 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",9)");
            String strActionInput4 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",10)");
            String strActionPanelText = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",11)");
            String strEvent = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",12)");
            String strExpectedQueue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",13)");
            String strScreenshotLocation = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",14)");
            String strScreenshotOnly = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",15)"); 
            
            //Login
            if (sp_Login(strUsername, strPassword) == false) {
                System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_Login)");
                return false;
            }

            //Navigate to queue or create claim
            if (sp_Workflow_Navigate(strActingAs, strCurrentQueue, strClaimantClaimNumber, strActionInput1, strActionInput2, strActionInput3, strActionInput4) == false) {
                System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_Workflow_Navigate)");
                return false;
            }
            
            //Check to take first second screenshot state
            if (strScreenshotOnly.equalsIgnoreCase("Y") || strScreenshotOnly.equalsIgnoreCase("Yes")) {
                automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimantClaimNumber + strActingAs + strCurrentQueue + strAction + "_before.png");
            }
            
            //Perform action and check it was successful - checking is commented out
            if (sp_PerformAction(strClaimantClaimNumber, strAction, strActionInput1, strActionInput2,
                    strActionInput3, strActionInput4, strActionPanelText, strEvent, strScreenshotLocation) == false) {
                //if action is not completed successfully skip checking queues for this claim.
                System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_PerformAction)");
                return false;
            }
            //Check to take second screenshot state
            if (strScreenshotOnly.equalsIgnoreCase("Y") || strScreenshotOnly.equalsIgnoreCase("Yes")) {
                automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimantClaimNumber + strActingAs + strCurrentQueue + strAction + "_after.png");
            }
            
            automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading");
            automationObject.genfunc_waitForAngular();
            
            /*Check claim is in the correct queue
            if (sp_Workflow_QueueCheck(strActingAs, strExpectedQueue, strClaimantClaimNumber) == false) {
                System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_Workflow_QueueCheck)");
            }
            */
            //log out
            sp_Logout();
        }

        return true;
    }

    public Boolean sp_Workflow_Navigate(String strActingAs, String strQueue, String strClaimantClaimNumber,
            String strActionInput1, String strActionInput2, String strActionInput3, String strActionInput4) {

        //Open Claim or create claim if the current queue is set to "None"
        if (!strQueue.equalsIgnoreCase("None")) {
            //Select Queue
            sp_SelectMenuQueue(strActingAs, strQueue);
            //Select Claim
            if (sp_SelectClaim(strClaimantClaimNumber) == false) {
                sp_Logout();
                return false;
            }
        } else {
            sp_SelectMenuQueue("Claimant", "Draft Claims");
            //create draft button
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div[2]/div/div/button");
            automationObject.genfunc_waitForAngular();
            //fill inputs
            sp_PopulateNewDraft(strClaimantClaimNumber, strActionInput1, strActionInput2, strActionInput3, strActionInput4);
        }
        automationObject.genfunc_waitForAngular();
        return true;
    }

    public Boolean sp_Workflow_QueueCheck(String strActingAs, String strQueue, String strClaimantClaimNumber) {
        System.out.println("    Checking claim '" + strClaimantClaimNumber + "' is in '" + strQueue + "' " + strActingAs + " queue.");
        //Check if claim is present in specified queue.
        if (!strQueue.equalsIgnoreCase("None")) {
            //Select Queue
            sp_SelectMenuQueue(strActingAs, strQueue);
            //Check Queue
            if (sp_IsClaimPresent(strClaimantClaimNumber, "Yes") == false) {
                return false;
            }
        } //Or check if claim is NOT present any queue if expected queue is "None".
        else {
            if (sp_IsClaimNotPresent(strClaimantClaimNumber, strActingAs) == false) {
                return false;
            }
        }
        return true;
    }

    /////////////////////////////////////////////
    ////            Subrogation Bulk Upload Methods
    /////////////////////////////////////////////
    public Boolean sp_BulkUpload_DataSetRunner(String DataSetName) {

        if (DataSetName.equalsIgnoreCase("")) {
            return false;
        }

        int iLastRow = 0;
        while (getDataSet("$UploadDataSet(" + iLastRow + ",1)").equalsIgnoreCase("") == false) {
            iLastRow++;
        }

        for (int iCurrentRow = 0; iCurrentRow < iLastRow; iCurrentRow++) {

            String strClaimantInsurer = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",2)");
            String strClaimantClaimNumber = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",3)");
            String strClaimantWorkgroup = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",4)");
            String strClaimantHandler = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",5)");
            String strClaimantPolicyNumber = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",6)");
            String strClaimantTitle = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",7)");
            String strClaimantFirstName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",8)");
            String strClaimantLastName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",9)");
            String strClaimantAddress1 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",10)");
            String strClaimantAddress2 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",11)");
            String strClaimantTown = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",12)");
            String strClaimantCounty = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",13)");
            String strClaimantPostcode = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",14)");

            String strDefendantInsurer = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",15)");
            String strDefendantClaimNumber = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",16)");
            String strDefendantWorkgroup = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",17)");
            String strDefendantHandler = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",18)");
            String strDefendantPolicyNumber = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",19)");
            String strDefendantTitle = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",20)");
            String strDefendantFirstName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",21)");
            String strDefendantLastName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",22)");
            String strDefendantAddress1 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",23)");
            String strDefendantAddress2 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",24)");
            String strDefendantTown = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",25)");
            String strDefendantCounty = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",26)");
            String strDefendantPostcode = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",27)");

            String strIncidentLocation = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",28)");
            String strIncidentDescription = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",29)");
            String strDamageDescription = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",30)");
            String strDateOfIncident = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",31)");
            String strTotalLoss = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",32)");
            String strCarUsable = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",33)");
            String strManagingRepair = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",34)");
            String strHireType = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",35)");

            String strClaimantVehicleMake = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",36)");
            String strClaimantVehicleModel = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",37)");
            String strClaimantVehicleVRN = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",38)");
            String strClaimantDriverTitle = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",39)");
            String strClaimantDriverFirstName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",40)");
            String strClaimantDriverLastName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",41)");

            String strDefendantVehicleMake = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",42)");
            String strDefendantVehicleModel = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",43)");
            String strDefendantVehicleVRN = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",44)");
            String strDefendantDriverTitle = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",45)");
            String strDefendantDriverFirstName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",46)");
            String strDefendantDriverLastName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",47)");

            String strPoliceInvolved = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 48)");
            String strCrimeRefNo = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 49)");
            String strPoliceOfficerName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 50)");
            String strPoliceOfficerID = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 51)");
            String strStationAddress1 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 52)");
            String strStationAddress2 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 53)");
            String strStationTown = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 54)");
            String strStationCounty = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 55)");
            String strStationPostcode = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 56)");

            String strWitnessName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 57)");
            String strWitnessAddress1 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 58)");
            String strWitnessAddress2 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 59)");
            String strWitnessTown = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 60)");
            String strWitnessCounty = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 61)");
            String strWitnessPostcode = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 62)");
            String strWitnessStatement = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 63)");

            if (strTotalLoss.toLowerCase().contains("n") || strTotalLoss.toLowerCase().contains("false")) {
                strTotalLoss = "No";
            } else if (strTotalLoss.toLowerCase().contains("y") || strTotalLoss.toLowerCase().contains("true")) {
                strTotalLoss = "Yes";
            }
            if (strCarUsable.toLowerCase().contains("n") || strCarUsable.toLowerCase().contains("false")) {
                strCarUsable = "No";
            } else if (strCarUsable.toLowerCase().contains("y") || strCarUsable.toLowerCase().contains("true")) {
                strCarUsable = "Yes";
            }
            if (strManagingRepair.toLowerCase().contains("n") || strManagingRepair.toLowerCase().contains("false")) {
                strManagingRepair = "No";
            } else if (strManagingRepair.toLowerCase().contains("y") || strManagingRepair.toLowerCase().contains("true")) {
                strManagingRepair = "Yes";
            }
            if (strPoliceInvolved.toLowerCase().contains("n") || strPoliceInvolved.toLowerCase().contains("false")) {
                strPoliceInvolved = "No";
            } else if (strPoliceInvolved.toLowerCase().contains("y") || strPoliceInvolved.toLowerCase().contains("true")) {
                strPoliceInvolved = "Yes";
            }

            sp_BulkUpload_Login(strDefendantInsurer, strClaimantClaimNumber);
            System.out.println("Selecting Claim Number " + strClaimantClaimNumber);

            sp_BulkUpload_ClaimSummaryFields(strClaimantInsurer, strClaimantClaimNumber, strClaimantWorkgroup, strClaimantHandler, strClaimantPolicyNumber, strClaimantTitle,
                    strClaimantFirstName, strClaimantLastName, strDefendantInsurer, strDefendantClaimNumber, strDefendantWorkgroup, strDefendantHandler, strDefendantPolicyNumber,
                    strDefendantTitle, strDefendantFirstName, strDefendantLastName, strClaimantAddress1, strClaimantAddress2, strClaimantTown, strClaimantCounty, strClaimantPostcode,
                    strDefendantAddress1, strDefendantAddress2, strDefendantTown, strDefendantCounty, strDefendantPostcode);

            sp_BulkUpload_IncidentDetailsFields(strIncidentLocation, strIncidentDescription, strDamageDescription, strDateOfIncident, strTotalLoss, strCarUsable, strManagingRepair,
                    strHireType, strClaimantVehicleMake, strClaimantVehicleModel, strClaimantVehicleVRN, strClaimantDriverTitle, strClaimantDriverFirstName, strClaimantDriverLastName,
                    strDefendantVehicleMake, strDefendantVehicleModel, strDefendantVehicleVRN, strDefendantDriverTitle, strDefendantDriverFirstName, strDefendantDriverLastName);

            sp_BulkUpload_PoliceDetailsFields(strPoliceInvolved, strCrimeRefNo, strPoliceOfficerName, strPoliceOfficerID, strStationAddress1, strStationAddress2, strStationTown,
                    strStationCounty, strStationPostcode);

            sp_BulkUpload_WitnessDetailsFields(strWitnessName, strWitnessAddress1, strWitnessAddress2, strWitnessTown, strWitnessCounty, strWitnessPostcode, strWitnessStatement);

            sp_Logout();
        }
        return true;
    }

    public Boolean sp_BulkUpload_Login(String strDefendantInsurer, String strClaimantClaimNumber) {

        if (strDefendantInsurer.equals("") || strDefendantInsurer.equals("null")) {
            System.out.println("ERROR - Claim '" + strClaimantClaimNumber + "' skipped as no username or/and password were provided.");
            return false;
        }
        String strUserOrg = strDefendantInsurer.toLowerCase();
        sp_Login("user1@" + strUserOrg + ".com", "password");
        sp_SelectMenuQueue("Defendant", "Assign Workgroup");
        sp_SelectClaim(strClaimantClaimNumber);

        return true;
    }

    public Boolean sp_BulkUpload_ClaimSummaryFields(String strClaimantInsurer, String strClaimantClaimNumber, String strClaimantWorkgroup, String strClaimantHandler, String strClaimantPolicyNumber, String strClaimantTitle,
            String strClaimantFirstName, String strClaimantLastName, String strDefendantInsurer, String strDefendantClaimNumber, String strDefendantWorkgroup, String strDefendantHandler, String strDefendantPolicyNumber,
            String strDefendantTitle, String strDefendantFirstName, String strDefendantLastName, String strClaimantAddress1, String strClaimantAddress2, String strClaimantTown, String strClaimantCounty, String strClaimantPostcode,
            String strDefendantAddress1, String strDefendantAddress2, String strDefendantTown, String strDefendantCounty, String strDefendantPostcode) {

        automationObject.genfunc_waitForAngular();
        //Claimant Insurer Details - All fields
        automationObject.genfunc_GetText("//*[@id=\"claimantInsurer\"]", strClaimantInsurer);
        automationObject.genfunc_GetText("//*[@id=\"claimantClaimNumber\"]", strClaimantClaimNumber);
        automationObject.genfunc_GetText("//*[@id=\"claimantWorkgroup\"]", strClaimantWorkgroup);
        automationObject.genfunc_GetText("//*[@id=\"claimantClaimHandler\"]", strClaimantHandler);
        automationObject.genfunc_GetText("//*[@id=\"claimantPolicyNumber\"]", strClaimantPolicyNumber);
        automationObject.genfunc_GetText("//*[@id=\"claimantTitle\"]", strClaimantTitle);
        automationObject.genfunc_GetText("//*[@id=\"claimantFirstName\"]", strClaimantFirstName);
        automationObject.genfunc_GetText("//*[@id=\"claimantLastName\"]", strClaimantLastName);

        //Defendant Insurer Details - All fields
        automationObject.genfunc_GetText("//*[@id=\"defendantInsurer\"]", strDefendantInsurer);
        automationObject.genfunc_GetText("//*[@id=\"defendantClaimNumber\"]", strDefendantClaimNumber);
        automationObject.genfunc_GetText("//*[@id=\"defendantWorkgroup\"]", strDefendantWorkgroup);
        automationObject.genfunc_GetText("//*[@id=\"defendantClaimHandler\"]", strDefendantHandler);
        automationObject.genfunc_GetText("//*[@id=\"defendantPolicyNumber\"]", strDefendantPolicyNumber);
        automationObject.genfunc_GetText("//*[@id=\"defendantTitle\"]", strDefendantTitle);
        automationObject.genfunc_GetText("//*[@id=\"defendantFirstName\"]", strDefendantFirstName);
        automationObject.genfunc_GetText("//*[@id=\"defendantLastName\"]", strDefendantLastName);

        //Address fields Claimant/Defendant
        //Toggle Expandable fields
        if (automationObject.genfunc_isElementPresent("//*[@id=\"claimantAddress1\"]") == false) {
            automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div/data-claim-summary/div/div[2]/div/div/div[1]/data-claimant-insurer-details/div/div/div[2]/div/div/div[10]/div/div/div[1]/p");
            automationObject.genfunc_waitForAngular();
        }
        if (automationObject.genfunc_isElementPresent("//*[@id=\"defendantAddress1\"]") == false) {
            automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div/data-claim-summary/div/div[2]/div/div/div[2]/data-defendant-insurer-details/div/div/div[2]/div/div/div[10]/div/div/div[1]/p");
            automationObject.genfunc_waitForAngular();
        }
        automationObject.genfunc_GetText("//*[@id=\"claimantAddress1\"]", strClaimantAddress1);
        automationObject.genfunc_GetText("//*[@id=\"claimantAddress2\"]", strClaimantAddress2);
        automationObject.genfunc_GetText("//*[@id=\"claimantTown\"]", strClaimantTown);
        automationObject.genfunc_GetText("//*[@id=\"claimantCounty\"]", strClaimantCounty);
        automationObject.genfunc_GetText("//*[@id=\"claimantPostcode\"]", strClaimantPostcode);

        automationObject.genfunc_GetText("//*[@id=\"defendantAddress1\"]", strDefendantAddress1);
        automationObject.genfunc_GetText("//*[@id=\"defendantAddress2\"]", strDefendantAddress2);
        automationObject.genfunc_GetText("//*[@id=\"defendantTown\"]", strDefendantTown);
        automationObject.genfunc_GetText("//*[@id=\"defendantCounty\"]", strDefendantCounty);
        automationObject.genfunc_GetText("//*[@id=\"defendantPostcode\"]", strDefendantPostcode);

        return true;
    }

    public Boolean sp_BulkUpload_IncidentDetailsFields(String strIncidentLocation, String strIncidentDescription, String strDamageDescription, String strDateOfIncident, String strTotalLoss, String strCarUsable, String strManagingRepair,
            String strHireType, String strClaimantVehicleMake, String strClaimantVehicleModel, String strClaimantVehicleVRN, String strClaimantDriverTitle, String strClaimantDriverFirstName, String strClaimantDriverLastName,
            String strDefendantVehicleMake, String strDefendantVehicleModel, String strDefendantVehicleVRN, String strDefendantDriverTitle, String strDefendantDriverFirstName, String strDefendantDriverLastName) {

        automationObject.genfunc_waitForAngular();
        //IncidentDetails
        automationObject.genfunc_GetText("//*[@id=\"incidentLocation\"]", strIncidentLocation);
        automationObject.genfunc_GetText("//*[@id=\"incidentDescription\"]", strIncidentDescription);
        automationObject.genfunc_GetText("//*[@id=\"damageDescription\"]", strDamageDescription);
        automationObject.genfunc_GetText("//*[@id=\"dateOfIncident\"]/div/input", strDateOfIncident);
        automationObject.genfunc_GetText("//*[@id=\"totalLoss\"]", strTotalLoss);
        automationObject.genfunc_GetText("//*[@id=\"carUsable\"]", strCarUsable);
        automationObject.genfunc_GetText("//*[@id=\"managingRepair\"]", strManagingRepair);
        automationObject.genfunc_GetText("//*[@id=\"hireType\"]", strHireType);

        automationObject.genfunc_GetText("//*[@id=\"claimantVehicleMake\"]", strClaimantVehicleMake);
        automationObject.genfunc_GetText("//*[@id=\"claimantVehicleModel\"]", strClaimantVehicleModel);
        automationObject.genfunc_GetText("//*[@id=\"claimantVehicleVRN\"]", strClaimantVehicleVRN);
        automationObject.genfunc_GetText("//*[@id=\"claimantDriverTitle\"]", strClaimantDriverTitle);
        automationObject.genfunc_GetText("//*[@id=\"claimantDriverFirstName\"]", strClaimantDriverFirstName);
        automationObject.genfunc_GetText("//*[@id=\"claimantDriverLastName\"]", strClaimantDriverLastName);

        automationObject.genfunc_GetText("//*[@id=\"defendantVehicleMake\"]", strDefendantVehicleMake);
        automationObject.genfunc_GetText("//*[@id=\"defendantVehicleModel\"]", strDefendantVehicleModel);
        automationObject.genfunc_GetText("//*[@id=\"defendantVehicleVRN\"]", strDefendantVehicleVRN);
        automationObject.genfunc_GetText("//*[@id=\"defendantDriverTitle\"]", strDefendantDriverTitle);
        automationObject.genfunc_GetText("//*[@id=\"defendantDriverFirstName\"]", strDefendantDriverFirstName);
        automationObject.genfunc_GetText("//*[@id=\"defendantDriverLastName\"]", strDefendantDriverLastName);

        return true;
    }

    public Boolean sp_BulkUpload_PoliceDetailsFields(String strPoliceInvolved, String strCrimeRefNo, String strPoliceOfficerName, String strPoliceOfficerID, String strStationAddress1, String strStationAddress2, String strStationTown,
            String strStationCounty, String strStationPostcode) {

        automationObject.genfunc_waitForAngular();

        automationObject.genfunc_GetText("//*[@id=\"policeInvolved\"]", strPoliceInvolved);
        automationObject.genfunc_GetText("//*[@id=\"crimRefNo\"]", strCrimeRefNo);
        automationObject.genfunc_GetText("//*[@id=\"policeOfficerName\"]", strPoliceOfficerName);
        automationObject.genfunc_GetText("//*[@id=\"policeOfficerID\"]", strPoliceOfficerID);
        automationObject.genfunc_GetText("//*[@id=\"stationAddressOne\"]", strStationAddress1);
        automationObject.genfunc_GetText("//*[@id=\"stationAddressTwo\"]", strStationAddress2);
        automationObject.genfunc_GetText("//*[@id=\"stationTown\"]", strStationTown);
        automationObject.genfunc_GetText("//*[@id=\"stationCounty\"]", strStationCounty);
        automationObject.genfunc_GetText("//*[@id=\"stationPostcode\"]", strStationPostcode);

        return true;
    }

    public Boolean sp_BulkUpload_WitnessDetailsFields(String strWitnessName, String strWitnessAddress1, String strWitnessAddress2, String strWitnessTown, String strWitnessCounty, String strWitnessPostcode, String strWitnessStatement) {

        automationObject.genfunc_waitForAngular();

        automationObject.genfunc_GetText("//*[@id=\"witnessName\"]", strWitnessName);
        automationObject.genfunc_GetText("//*[@id=\"witnessAddressOne\"]", strWitnessAddress1);
        automationObject.genfunc_GetText("//*[@id=\"witnessAddressTwo\"]", strWitnessAddress2);
        automationObject.genfunc_GetText("//*[@id=\"witnessTown\"]", strWitnessTown);
        automationObject.genfunc_GetText("//*[@id=\"witnessCounty\"]", strWitnessCounty);
        automationObject.genfunc_GetText("//*[@id=\"witnessPostcode\"]", strWitnessPostcode);
        automationObject.genfunc_GetText("//*[@id=\"witnessStatement\"]", strWitnessStatement);

        return true;
    }

    /////////////////////////////////////////////
    ////            Subrogation Admin/User Methods
    /////////////////////////////////////////////
    public Boolean sp_Admin_CreateUsers(String DataSetName) {

        if (DataSetName.equalsIgnoreCase("")) {
            return false;
        }

        //To find the last row in the data set
        int iLastRow = 0;
        while (getDataSet("$" + DataSetName + "(" + iLastRow + ",1)").equalsIgnoreCase("") == false) {
            iLastRow++;
        }

        //Click Admin tab
        automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Admin\")]");
        automationObject.genfunc_waitForAngular();
        //Click User Admin sub tab
        automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[4]/ul/li[contains(.,\"User Admin\")]/a");
        automationObject.genfunc_waitForAngular();

        for (int iCurrentRow = 0; iCurrentRow < iLastRow; iCurrentRow++) {
            String strOrganisation = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",1)");
            String strUsername = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",2)");
            String strEmailAddress = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",3)");
            String strFirstName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",4)");
            String strSurname = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",5)");
            String strWorkgroup = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",6)");
            String strRole;
            Integer iCurrentRolesColumn = 7;
            try {
                //Select the organisation to add the user to
                automationObject.genfunc_Click("//*[@id=\"organisation\"]/option[contains(.,\"" + strOrganisation + "\")]");
                automationObject.genfunc_waitForAngular();
                //Click the "Add New User" button
                automationObject.genfunc_Click("//button[contains(.,\"Add New User\")]");
                automationObject.genfunc_waitForAngular();
                //Fill in new user details
                automationObject.genfunc_SetText("//*[@id=\"userName\"]", strUsername);
                automationObject.genfunc_SetText("//*[@id=\"email\"]", strEmailAddress);
                automationObject.genfunc_SetText("//*[@id=\"firstName\"]", strFirstName);
                automationObject.genfunc_SetText("//*[@id=\"lastName\"]", strSurname);
                //Select the 'Active' tickbox
                automationObject.genfunc_Click("//*[@id=\"userDetailsTabId\"]/div[2]/div/div/div[1]/div/div/div[2]/div/div/div/div[7]/div/div/div/div/label/input");
                //Click the Create User button
                automationObject.genfunc_Click("//*[@id=\"userDetailsTabId\"]/div[3]/a[contains(.,\"Create\")]");
                automationObject.genfunc_waitForAngular();
                //If there is any validation messages print error and move on to creating next user
                if (automationObject.genfunc_isElementPresent("//*[@id=\"userDetailsTabId\"]/div[2]/div/div/div[2]/div/div") == true) {
                    System.out.println("ERROR - " + strUsername + " not created.");
                    //Navigate back to User Admin tab
                    automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading[contains(.,\"User Admin\")]");
                    automationObject.genfunc_waitForAngular();
                    continue;
                }
                //Navigate to roles tab
                automationObject.genfunc_Click("//*[@id=\"userTabsId\"]/a/tab-heading[contains(.,\"Roles\")]");
                //Add roles
                iCurrentRolesColumn = 7;
                while (!getDataSet("$" + DataSetName + "(" + iCurrentRow + "," + iCurrentRolesColumn + ")").equalsIgnoreCase("")) {
                    strRole = getDataSet("$CreateUsersDataSet(" + iCurrentRow + "," + iCurrentRolesColumn + ")");
                    automationObject.genfunc_Click("//td[.=\"" + strRole + "\"]");
                    //automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/tbody/tr[.=\"" + strRole + "\"]");
                    iCurrentRolesColumn = iCurrentRolesColumn + 1;
                }
                //Move roles over to Assigned Roles listbox
                automationObject.genfunc_Click("//*[@id=\"userRolesTabId\"]/div[2]/div/div/div[2]/div[1]/span/i");
                //Save roles
                automationObject.genfunc_Click("//*[@id=\"userRolesTabId\"]/div[3]/a[contains(.,\"Save\")]");
                automationObject.genfunc_waitForAngular();
                //Add user to workgroup (if applicable)
                if (!strWorkgroup.equalsIgnoreCase("")) {
                    //Navigate back to Workgroups tab
                    automationObject.genfunc_Click("//*[@id=\"userTabsId\"]/a/tab-heading[contains(.,\"Workgroups\")]");
                    //Clcik on workgroup
                    automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/tbody/tr[contains(.,\"" + strWorkgroup + "\")]");
                    //Move workgroup over to Assigned Workgroup listbox
                    automationObject.genfunc_Click("//*[@id=\"userWorkgroupsTabId\"]/div[2]/div/div/div[2]/div[1]/span/i");
                    //Save workgroups
                    automationObject.genfunc_Click("//*[@id=\"userWorkgroupsTabId\"]/div[3]/a[contains(.,\"Save\")]");
                    automationObject.genfunc_waitForAngular();
                }
                //Close the tab
                automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading[contains(.,\"" + strUsername + "\")]/i[2]");
                automationObject.genfunc_waitForAngular();
                //Navigate back to User Admin tab
                automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading[contains(.,\"User Admin\")]");
                automationObject.genfunc_waitForAngular();
            } catch (Exception e) {
                System.out.println("ERROR - " + strUsername + " not created.");
                //Navigate back to User Admin tab
                automationObject.genfunc_RefreshPage();
                automationObject.genfunc_waitForAngular();
                if (automationObject.genfunc_isElementPresent("//*[@id=\"claimantTabsId\"]/a/tab-heading[contains(.,\"" + strUsername + "\")]/i[2]") == true) {
                    //Close the tab if present
                    automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading[contains(.,\"" + strUsername + "\")]/i[2]");
                    automationObject.genfunc_waitForAngular();
                }
                automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading[contains(.,\"User Admin\")]");
                automationObject.genfunc_waitForAngular();
            }
        }

        return true;
    }

    public Boolean sp_Admin_CheckPermissions(String DataSetName) {
        if (DataSetName.equalsIgnoreCase("")) {
            return false;
        }

        //To find the last row in the data set
        int iLastRow = 0;
        while (getDataSet("$" + DataSetName + "(" + iLastRow + ",1)").equalsIgnoreCase("") == false) {
            iLastRow++;
        }

        for (int iCurrentRow = 0; iCurrentRow < iLastRow; iCurrentRow++) {
            String strUsername = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",1)");
            String strPassword = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",2)");

            String strWorkQueuesValue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",3)");
            String strClaimantQueuesValue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",4)");
            String strClaimant_DC_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",5)");
            String strClaimant_AW_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",6)");
            String strClaimant_AO_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",7)");
            String strClaimant_RC_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",8)");
            String strClaimant_AI_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",9)");
            String strClaimant_DI_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",10)");
            String strClaimant_QI_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",11)");
            String strClaimant_PI_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",12)");

            String strDefendantQueuesValue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",13)");
            String strDefendant_AW_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",14)");
            String strDefendant_AO_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",15)");
            String strDefendant_CR_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",16)");
            String strDefendant_AA_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",17)");
            String strDefendant_PQ_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",18)");
            String strDefendant_PL_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",19)");
            String strDefendant_PP_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",20)");

            String strSearchValue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",21)");
            String strNettingValue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",22)");
            String strAdminValue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",23)");
            String strAdmin_Organisation_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",24)");
            String strAdmin_User_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",25)");
            String strUploadValue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",26)");

            System.out.println("    Checking " + strUsername + " permissions.");

            if (sp_Login(strUsername, strPassword) == false) {
                System.out.println("ERROR - Failed to login.");
                continue;
            }

            //Work Queues
            sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]", strWorkQueuesValue, "Work Queues Menu Option");
            if (automationObject.genfunc_isElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]") == true) {
                automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]");
                automationObject.genfunc_waitForAngular();
                //Claimant Work Queues
                sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Claimant Queues\")]", strClaimantQueuesValue, "Claimant Queues Menu Option");
                if (automationObject.genfunc_isElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Claimant Queues\")]") == true) {
                    automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Claimant Queues\")]");
                    automationObject.genfunc_waitForAngular();
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Draft Claims\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_DC_Value, "Claimant Queue: Draft Claims");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Assign Workgroup\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_AW_Value, "Claimant Queue: Assign Workgroup");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Assign Owner\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_AO_Value, "Claimant Queue: Assign Owner");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Rejected Claims\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_RC_Value, "Claimant Queue: Reject Claims");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Awaiting Invoice\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_AI_Value, "Claimant Queue: Awaiting Invoice");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Draft Invoices\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_DI_Value, "Claimant Queue: Draft Invoices");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Queried Invoices\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_QI_Value, "Claimant Queue: Queried Invoices");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Paid Invoices\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_PI_Value, "Claimant Queue: Paid Invoices");
                }
                //Defendant Work Queues
                sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Defendant Queues\")]", strDefendantQueuesValue, "Defendant Queues Menu Option");
                if (automationObject.genfunc_isElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Defendant Queues\")]") == true) {
                    automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Defendant Queues\")]");
                    automationObject.genfunc_waitForAngular();
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Assign Workgroup\")]/div[1]/div/div/div[2]/div[1]/div", strDefendant_AW_Value, "Defendant Queue: Assign Workgroup");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Assign Owner\")]/div[1]/div/div/div[2]/div[1]/div", strDefendant_AO_Value, "Defendant Queue: Assign Owner");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Claims to be Registered\")]/div[1]/div/div/div[2]/div[1]/div", strDefendant_CR_Value, "Defendant Queue: Claims to be Registered");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Awaiting Acknowledgement\")]/div[1]/div/div/div[2]/div[1]/div", strDefendant_AA_Value, "Defendant Queue: Awaiting Acknowledgement");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Pending Quantum\")]/div[1]/div/div/div[2]/div[1]/div", strDefendant_PQ_Value, "Defendant Queue: Pending Quantum");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Pending Liability\")]/div[1]/div/div/div[2]/div[1]/div", strDefendant_PL_Value, "Defendant Queue: Pending Liability");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Pending Payment\")]/div[1]/div/div/div[2]/div[1]/div", strDefendant_PP_Value, "Defendant Queue: Pending Payment");
                }
            }

            sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Search\")]", strSearchValue, "Search Menu Option");
            sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Netting\")]", strNettingValue, "Netting Menu Option");
            sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Admin\")]", strAdminValue, "Admin Menu Option");
            if (automationObject.genfunc_isElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Admin\")]") == true) {
                automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Admin\")]");
                automationObject.genfunc_waitForAngular();
                sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Organisation Admin\")]/ul/li[contains(.,\"Organisation Admin\")]", strAdmin_Organisation_Value, "Organisation Admin Menu Option");
                sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"User Admin\")]/ul/li[contains(.,\"User Admin\")]", strAdmin_User_Value, "User Admin Menu Option");
            }
            sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Upload\")]", strUploadValue, "Upload Menu Option");
            sp_Logout();
        }
        return true;
    }

    /////////////////////////////////////////////
    ////            Subrogation Tasks Methods
    /////////////////////////////////////////////
    public Boolean sp_Tasks_DataSetRunner(String DataSetName) {
        
        if (DataSetName.equalsIgnoreCase("")) {
            return false;
        }
        
        //To find the last row in the data set
        int iLastRow = 0;
        while (getDataSet("$" + DataSetName + "(" + iLastRow + ",1)").equalsIgnoreCase("") == false) {
            iLastRow++;
        }

        for (int iCurrentRow = 0; iCurrentRow < iLastRow; iCurrentRow++) {
           
            //Get values from data set      
            String strUsername              = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",1)");
            String strPassword              = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",2)");
            String strTaskType              = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",3)");
            String strTaskDescription       = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",4)");
            String strClaimNumber           = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",5)");
            String strDueDays               = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",6)");
            String strAction                = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",7)");
            String strActionInput1          = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",8)");
            String strActionInput2          = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",9)");
            String strActionInput3          = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",10)");
            String strActionInput4          = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",11)");
            String strActionPanelText       = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",12)");
            String strEvent                 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",13)");
            String strCompletionStatus      = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",14)");
            String strScreenshotLocation    = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",15)");
           
            //Convert due days to an integer
            int iDueDays  = 0;
            if (!strDueDays.equalsIgnoreCase("")){
                iDueDays  = Integer.parseInt(strDueDays);
            }
            
            automationObject.genfunc_waitForAngular();
            
            //Login
            if (sp_Login(strUsername, strPassword) == false) {
                System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_Login)");
                return false;
            }
            
            //Navigate to tasks page or create claim
            if (sp_Tasks_Navigate(strTaskDescription, strClaimNumber, strAction, strActionInput1, strActionInput2, strActionInput3, 
                    strActionInput4, strActionPanelText, strEvent, strScreenshotLocation) == false){
                System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_Tasks_Navigate)");
                return false;
            }
            
            //Find claim,check row data, and open the task
             if (sp_Tasks_SelectTask(strTaskType, strTaskDescription, strClaimNumber, iDueDays, strAction, strScreenshotLocation) == false){
                System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_Tasks_SelectTask)");
                return false;
            }

            if(!strTaskDescription.equalsIgnoreCase("None")){ //skips performing the action if the Task Description is equal to None i.e. there is no task
                //Perform action and check it was successful
                if (sp_PerformAction(strClaimNumber, strAction, strActionInput1, strActionInput2, strActionInput3, 
                        strActionInput4, strActionPanelText, strEvent, strScreenshotLocation) == false) {
                    //if action is not completed successfully skip checking queues for this claim.
                    System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_PerformAction)");
                    return false;
                }
            }
            
            //Check task has been completed
            sp_Tasks_CheckCompleted(strTaskDescription, strClaimNumber, strCompletionStatus, strAction, strScreenshotLocation);
            
            //Logout
            sp_Logout();
        }

        return true;
    }
    
    public Boolean sp_Tasks_Navigate(String strTaskDescription, String strClaimNumber, String strAction, String strActionInput1, String strActionInput2,
            String strActionInput3, String strActionInput4, String strActionPanelText, String strEvent, String strScreenshotLocation){
        
        //Open Claim or create claim if the current queue is set to "None"
        if (!strTaskDescription.equalsIgnoreCase("None")) {
            //navigate to Tasks page
            if (sp_SelectMenuQueue("Tasks", "") == false){
                sp_Logout();
                return false;
            }
            
        } else {
            sp_SelectMenuQueue("Claimant", "Draft Claims");
            //create draft button
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div[2]/div/div/button");
            automationObject.genfunc_waitForAngular();
            //fill inputs
            sp_PopulateNewDraft(strClaimNumber, strActionInput1, strActionInput2, strActionInput3, strActionInput4);
            //submit claim
            if (sp_PerformAction(strClaimNumber, strAction, strActionInput1, strActionInput2,
            strActionInput3, strActionInput4, strActionPanelText, strEvent, strScreenshotLocation) == false){
                return false;
            }
        }
        automationObject.genfunc_waitForAngular();
        return true;
    }    
    
    public Boolean sp_Tasks_SelectTask(String strTaskType, String strTaskDescription, String strClaimNumber, Integer iDueDays, String strAction, String strScreenshotLocation) {
        
        if (strTaskDescription.equalsIgnoreCase("None")){
            return true;
        }
        
        int iPage = 1;
        //select the dropdown to display 100 results per page
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/label/select/option[contains(.,\"100\")]");
        automationObject.genfunc_waitForAngular();
        
        //Table Columns
        int iOpenButtonCol = 1;
        int iTypeCol = 2;
        int iDescriptionCol = 3;
        int iClaimNumberCol = 4;
        int iCreatedDateCol = 8;
        int iDueDateCol = 9;
        
        //Xpath contains task description and claim number
        String strTaskXPath = "//*[starts-with(@id,\"DataTables_Table\")]/tbody/tr[contains(.,\"" + strTaskDescription + "\") and contains(.,\"" + strClaimNumber + "\")]";

        //Find the created and due dates
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //Set date format
        Calendar cal = Calendar.getInstance(); //Set the calandar date to todays date
        Date CurrentDate = cal.getTime(); //Get the calander date
        cal.add(Calendar.DATE, iDueDays); //Add 'iDueDays' days to the calander date
        Date DueDate = cal.getTime(); //Get the calander date
        String strCreatedDate = sdf.format(CurrentDate); //Change the date to the correct format
        String strDueDate = sdf.format(DueDate); //Change the date to the correct format
           
        //Cycle through table pages
        while (automationObject.genfunc_isElementPresent("//*[starts-with(@id,\"DataTables_Table\")]/span/a[" + iPage + "]")) {
            sp_SelectTablePage(iPage);
            automationObject.genfunc_waitForAngular();
            //Search the column for the specific claim number
            if (automationObject.genfunc_isElementPresent(strTaskXPath + "/td[" + iOpenButtonCol + "]/i") == true) {
  
                //Check data values
                if (automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iTypeCol + "]", strTaskType) == false){
                    System.out.println("Failed - Task Type is not as expected. See screenshot for more details.");
                }
                if (automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iDescriptionCol + "]", strTaskDescription) == false){
                    System.out.println("Failed - Task Description is not as expected. See screenshot for more details.");
                }
                if (automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iClaimNumberCol + "]", strClaimNumber) == false){
                    System.out.println("Failed - Task Claim Number is not as expected. See screenshot for more details.");
                }
                if (automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iCreatedDateCol + "]", strCreatedDate) == false){
                    System.out.println("Failed - Task Created Date is not as expected. See screenshot for more details.");
                }
                if (automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iDueDateCol + "]", strDueDate) == false){
                    System.out.println("Failed - Task Due Date is not as expected. See screenshot for more details.");
                }
                
                //Take screenshot if any values failed
                if (automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iTypeCol + "]", strTaskType) == false ||
                automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iDescriptionCol + "]", strTaskDescription) == false ||
                automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iClaimNumberCol + "]", strClaimNumber) == false ||
                automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iCreatedDateCol + "]", strCreatedDate) == false ||
                automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iDueDateCol + "]", strDueDate) == false){
                    automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimNumber + " - " + strAction + " - TasksTableData.png");
                }
                
                //Open the claim 
                automationObject.genfunc_Click(strTaskXPath + "/td[" + iOpenButtonCol + "]/i");
                return true;
            }
            iPage = iPage + 1;
        }
        System.out.println("ERROR - Could not select claim '" + strClaimNumber + "' with description '" + strTaskDescription + "'.");
        sp_Logout();
        return false;
    }
    
    public Boolean sp_Tasks_CheckCompleted(String strTaskDescription, String strClaimNumber, String strCompletionStatus, String strAction, String strScreenshotLocation) {
        
        if (strTaskDescription.equalsIgnoreCase("None")){
            return true;
        }
        
        System.out.println("    Checking task with claim number '" + strClaimNumber + "' and task description '" + strTaskDescription + "' has been completed.");
        int iPage = 1;
        //select the dropdown to display 100 results per page
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/label/select/option[contains(.,\"100\")]");
        automationObject.genfunc_waitForAngular();
        
        //Table Columns
        int iOpenButtonCol = 1; 
        int iCompletedDateCol = 11;
        int iCompletionStatusCol =13;
        
        //Xpath contains task description and claim number
        String strTaskXPath = "//*[starts-with(@id,\"DataTables_Table\")]/tbody/tr[contains(.,\"" + strTaskDescription + "\") and contains(.,\"" + strClaimNumber + "\")]";
        
        //Display completed tasks as well
        automationObject.genfunc_Click("//button[contains(.,\"Show Completed Tasks\")]");
        
        //Find the created and due dates
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //Set date format
        Calendar cal = Calendar.getInstance(); //Set the calandar date to todays date
        Date CurrentDate = cal.getTime(); //Get the calander date
     //   cal.add(Calendar.DATE, iDueDays); //Add 'iDueDays' days to the calander date
     //   Date DueDate = cal.getTime(); //Get the calander date
        String strCreatedDate = sdf.format(CurrentDate); //Change the date to the correct format
     //   String strDueDate = sdf.format(DueDate); //Change the date to the correct format
        
        if (strCompletionStatus.equalsIgnoreCase("None")){
            //Cycle through table pages
            while (automationObject.genfunc_isElementPresent("//*[starts-with(@id,\"DataTables_Table\")]/span/a[" + iPage + "]")) {
                sp_SelectTablePage(iPage);
                automationObject.genfunc_waitForAngular();
                //Search the column for the specific claim number and task description
                if (automationObject.genfunc_isElementPresent(strTaskXPath + "/td[" + iOpenButtonCol + "]/i") == true) {
                    
                    if (
                    automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iCompletedDateCol  + "]","") == false ||
                    automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iCompletionStatusCol   + "]", "") == false){   
                        System.out.println("Failed - Task has been moved to completed. See screenshot for more details.");
                        automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimNumber + " - " + strAction + " - TaskCompletion.png");
                    }
                    break;
                }
                iPage = iPage + 1;
            }   
        }
        else{
            //Cycle through table pages
            while (automationObject.genfunc_isElementPresent("//*[starts-with(@id,\"DataTables_Table\")]/span/a[" + iPage + "]")) {
                sp_SelectTablePage(iPage);
                automationObject.genfunc_waitForAngular();
                //Search the column for the specific claim number and task description
                if (automationObject.genfunc_isElementPresent(strTaskXPath + "/td[" + iOpenButtonCol + "]/i") == true) {
                    
                    if(
                    automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iCompletedDateCol  + "]", strCreatedDate) == false ||
                    automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iCompletionStatusCol   + "]", strCompletionStatus) == false){
                        System.out.println("Failed - Task has NOT been moved to completed. See screenshot for more details.");
                        automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimNumber + " - " + strAction + " - TaskCompletion.png");
                    }
                    break;
                }
                iPage = iPage + 1;
            }
            
        }
        return true;
    }

}
