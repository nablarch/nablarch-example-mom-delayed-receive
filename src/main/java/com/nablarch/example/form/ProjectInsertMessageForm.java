package com.nablarch.example.form;

import java.util.Date;

import nablarch.core.dataformat.DataRecord;
import nablarch.core.db.statement.autoproperty.CurrentDateTime;
import nablarch.core.db.statement.autoproperty.RequestId;
import nablarch.core.db.statement.autoproperty.UserId;
import nablarch.core.util.DateUtil;
import nablarch.core.util.StringUtil;
import nablarch.fw.messaging.RequestMessage;

/**
 * プロジェクト情報を保持するフォームクラス。
 */
public class ProjectInsertMessageForm {

    /** 受信電文連番 */
    private String receivedMessageSequence;

    /** プロジェクト名 */
    private String projectName;

    /** プロジェクト種別 */
    private String projectType;

    /** プロジェクト分類 */
    private String projectClass;

    /** プロジェクト開始日付 */
    private Date projectStartDate;

    /** プロジェクト終了日付 */
    private Date projectEndDate;

    /** 顧客ID */
    private String clientId;

    /** プロジェクトマネージャー */
    private String projectManager;

    /** プロジェクトリーダー */
    private String projectLeader;

    /** ユーザID */
    private String userId;

    /** 備考 */
    private String note;

    /** 売上高 */
    private Integer sales;

    /** 売上原価 */
    private Integer costOfGoodsSold;

    /** 販管費 */
    private Integer sga;

    /** 本社配賦 */
    private Integer allocationOfCorpExpenses;

    /** 登録ユーザID */
    @UserId
    private String insertUserId;

    /** 登録日時 */
    @CurrentDateTime
    private java.sql.Timestamp insertDate;

    /** 登録リクエストID */
    @RequestId
    private String insertRequestId;

    /** 更新ユーザID */
    @UserId
    private String updatedUserId;

    /** 更新日時 */
    @CurrentDateTime
    private java.sql.Timestamp updatedDate;

    /**
     * コンストラクタ。<br />
     *
     * 本コンストラクタを使用してフレームワークはクラスをインスタンス化する。
     *
     * @param receivedMessageSequence 受信電文連番(フレームワークがID_GENERATEテーブルを使用して採番したIDが渡される)
     * @param message 受信電文
     */
    public ProjectInsertMessageForm(String receivedMessageSequence, RequestMessage message) {
        this.receivedMessageSequence = receivedMessageSequence;

        DataRecord data = message.getRecordOf("userData");

        projectName = data.getString("projectName");
        projectType = data.getString("projectType");
        projectClass = data.getString("projectClass");

        String projectStartDateSt = data.getString("projectStartDate");
        if (StringUtil.hasValue(projectStartDateSt)) {
            projectStartDate = DateUtil.getParsedDate(projectStartDateSt, "yyyyMMdd");
        }

        String projectEndDateSt = data.getString("projectEndDate");
        if (StringUtil.hasValue(projectEndDateSt)) {
            projectEndDate = DateUtil.getParsedDate(projectEndDateSt, "yyyyMMdd");
        }

        projectClass = data.getString("projectClass");
        clientId = data.getString("clientId");
        projectManager = data.getString("projectManager");
        projectLeader = data.getString("projectLeader");
        userId = data.getString("userId");
        note = data.getString("note");

        String salesSt = data.getString("sales");
        if (StringUtil.hasValue(salesSt)) {
            sales = Integer.valueOf(salesSt);
        }

        String costOfGoodsSoldSt = data.getString("costOfGoodsSold");
        if (StringUtil.hasValue(costOfGoodsSoldSt)) {
            costOfGoodsSold = Integer.valueOf(costOfGoodsSoldSt);
        }

        String sgaSt = data.getString("sga");
        if (StringUtil.hasValue(sgaSt)) {
            sga = Integer.valueOf(sgaSt);
        }

        String allocationOfCorpExpensesSt = data.getString("allocationOfCorpExpenses");
        if (StringUtil.hasValue(allocationOfCorpExpensesSt)) {
            allocationOfCorpExpenses = Integer.valueOf(allocationOfCorpExpensesSt);
        }
    }

    /**
     * 受信電文連番を取得する。
     * @return 受信電文連番
     */
    public String getReceivedMessageSequence() {
        return receivedMessageSequence;
    }

    /**
     * プロジェクト名を取得する。
     * @return プロジェクト名
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * プロジェクト種別を取得する。
     * @return プロジェクト種別
     */
    public String getProjectType() {
        return projectType;
    }

    /**
     * プロジェクト分類を取得する。
     * @return プロジェクト分類
     */
    public String getProjectClass() {
        return projectClass;
    }

    /**
     * プロジェクト開始日付を取得する。
     * @return プロジェクト開始日付
     */
    public Date getProjectStartDate() {
        return projectStartDate;
    }

    /**
     * プロジェクト終了日付を取得する。
     * @return プロジェクト終了日付
     */
    public Date getProjectEndDate() {
        return projectEndDate;
    }

    /**
     * 顧客IDを取得する。
     * @return 顧客ID
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * プロジェクトマネージャーを取得する。
     * @return プロジェクトマネージャー
     */
    public String getProjectManager() {
        return projectManager;
    }

    /**
     * プロジェクトリーダーを取得する。
     * @return プロジェクトリーダー
     */
    public String getProjectLeader() {
        return projectLeader;
    }

    /**
     * ユーザIDを取得する。
     * @return ユーザID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 備考を取得する。
     * @return 備考
     */
    public String getNote() {
        return note;
    }

    /**
     * 売上高を取得する。
     * @return 売上高
     */
    public Integer getSales() {
        return sales;
    }

    /**
     * 売上原価を取得する。
     * @return 売上原価
     */
    public Integer getCostOfGoodsSold() {
        return costOfGoodsSold;
    }

    /**
     * 販管費を取得する。
     * @return 販管費
     */
    public Integer getSga() {
        return sga;
    }

    /**
     * 本社配賦を取得する。
     * @return 本社配賦
     */
     public Integer getAllocationOfCorpExpenses() {
        return allocationOfCorpExpenses;
    }

    /**
     * 登録ユーザIDを取得する。
     * @return 登録ユーザID
     */
    public String getInsertUserId() {
        return insertUserId;
    }

    /**
     * 登録日時を取得する。
     * @return 登録日時
     */
    public java.sql.Timestamp getInsertDate() {
        return insertDate;
    }

    /**
     * 登録リクエストIDを取得する。
     * @return 登録リクエストID
     */
    public String getInsertRequestId() {
        return insertRequestId;
    }

    /**
     * 更新ユーザIDを取得する。
     * @return 更新ユーザID
     */
    public String getUpdatedUserId() {
        return updatedUserId;
    }

    /**
     * 更新日時を取得する。
     * @return 更新日時
     */
    public java.sql.Timestamp getUpdatedDate() {
        return updatedDate;
    }
}
