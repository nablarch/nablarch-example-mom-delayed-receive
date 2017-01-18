package com.nablarch.example.form;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import nablarch.core.beans.BeanUtil;
import nablarch.core.dataformat.DataRecord;
import nablarch.core.db.statement.autoproperty.CurrentDateTime;
import nablarch.core.db.statement.autoproperty.RequestId;
import nablarch.core.db.statement.autoproperty.UserId;
import nablarch.fw.messaging.RequestMessage;

/**
 * プロジェクト情報を保持するフォームクラス。
 *
 * @author Nabu Rakutaro
 */
public class ProjectInsertMessageForm {

    /** 受信電文連番 */
    private final String receivedMessageSequence;

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
    private Timestamp insertDate;

    /** 登録リクエストID */
    @RequestId
    private String insertRequestId;

    /** 更新ユーザID */
    @UserId
    private String updatedUserId;

    /** 更新日時 */
    @CurrentDateTime
    private Timestamp updatedDate;

    /**
     * コンストラクタ。<br />
     *
     * 本コンストラクタを使用してフレームワークはクラスをインスタンス化する。
     *
     * @param receivedMessageSequence 受信電文連番(フレームワークがID_GENERATEテーブルを使用して採番したIDが渡される)
     * @param message 受信電文
     */
    public ProjectInsertMessageForm(final String receivedMessageSequence, final RequestMessage message) {
        this.receivedMessageSequence = receivedMessageSequence;
        final DataRecord data = message.getRecordOf("userData");
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            BeanUtil.setProperty(this, entry.getKey(), entry.getValue());
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
    public Timestamp getInsertDate() {
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
    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    /**
     * プロジェクト名を設定する。
     * @param projectName プロジェクト名
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * プロジェクト種別を設定する。
     * @param projectType プロジェクト種別
     */
    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    /**
     * プロジェクト分類を設定する。
     * @param projectClass プロジェクト分類
     */
    public void setProjectClass(String projectClass) {
        this.projectClass = projectClass;
    }

    /**
     * プロジェクト開始日付を設定する。
     * @param projectStartDate プロジェクト開始日付
     */
    public void setProjectStartDate(Date projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    /**
     * プロジェクト終了日付を設定する。
     * @param projectEndDate プロジェクト終了日付
     */
    public void setProjectEndDate(Date projectEndDate) {
        this.projectEndDate = projectEndDate;
    }

    /**
     * クライアントIDを設定する。
     * @param clientId クライアントID
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * プロジェクトマネージャを設定する。
     * @param projectManager プロジェクトマネージャ
     */
    public void setProjectManager(String projectManager) {
        this.projectManager = projectManager;
    }

    /**
     * プロジェクトリーダを設定する。
     * @param projectLeader プロジェクトリーダ
     */
    public void setProjectLeader(String projectLeader) {
        this.projectLeader = projectLeader;
    }

    /**
     * ユーザIDを設定する。
     * @param userId ユーザ
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 備考を設定する。
     * @param note 備考
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * 売上高を設定する。
     * @param sales 売上高
     */
    public void setSales(Integer sales) {
        this.sales = sales;
    }

    /**
     * 売上原価を設定する。
     * @param costOfGoodsSold 売上原価
     */
    public void setCostOfGoodsSold(Integer costOfGoodsSold) {
        this.costOfGoodsSold = costOfGoodsSold;
    }

    /**
     * 販売費を設定する。
     * @param sga 販売費
     */
    public void setSga(Integer sga) {
        this.sga = sga;
    }

    /**
     * 本社配賦を設定する。
     * @param allocationOfCorpExpenses 本社配賦
     */
    public void setAllocationOfCorpExpenses(Integer allocationOfCorpExpenses) {
        this.allocationOfCorpExpenses = allocationOfCorpExpenses;
    }
}
