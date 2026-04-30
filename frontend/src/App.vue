<template>
  <main class="page">
    <section class="hero">
      <div>
        <p class="eyebrow">LLM Powered BI Assistant</p>
        <h1>Text-to-SQL 数据分析助手</h1>
        <p class="subtitle">输入业务问题，自动生成安全 SQL、查询结果说明与 ECharts 图表。</p>
      </div>
    </section>

    <section class="panel query-panel">
      <div class="form-row">
        <label>
          数据源
          <select v-model="datasourceId">
            <option v-for="item in datasources" :key="item.id" :value="item.id">
              {{ item.name }} / {{ item.bizDomain }}
            </option>
          </select>
        </label>
        <label>
          图表类型
          <select v-model="chartType">
            <option value="line">折线图</option>
            <option value="bar">柱状图</option>
            <option value="table">表格</option>
          </select>
        </label>
      </div>
      <textarea v-model="question" placeholder="例如：统计最近30天每天新增用户数，并画成折线图" />
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      <div class="actions">
        <button :disabled="loading || !question.trim()" @click="submitQuery">
          {{ loading ? '分析中...' : '开始分析' }}
        </button>
      </div>
    </section>

    <section v-if="result?.needClarify" class="panel clarify-panel">
      <h2>需要补充信息</h2>
      <p>{{ result.clarifyQuestion }}</p>
    </section>

    <section v-if="result && !result.needClarify" class="grid">
      <div class="panel">
        <h2>生成 SQL</h2>
        <pre>{{ result.sql }}</pre>
      </div>
      <div class="panel">
        <h2>结果解释</h2>
        <p>{{ result.explanation }}</p>
      </div>
      <div class="panel table-panel">
        <h2>查询结果</h2>
        <table v-if="result.rows?.length">
          <thead>
            <tr>
              <th v-for="column in result.columns" :key="column.name">{{ column.label }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, index) in result.rows" :key="index">
              <td v-for="column in result.columns" :key="column.name">{{ row[column.name] }}</td>
            </tr>
          </tbody>
        </table>
        <p v-else class="empty">暂无数据</p>
      </div>
      <ChartPanel v-if="chartType !== 'table'" class="panel" :option="result.chartOption" />
    </section>

    <section class="panel metadata-panel">
      <div class="section-title">
        <div>
          <p class="eyebrow dark">Metadata Foundation</p>
          <h2>第二阶段：元数据基础</h2>
        </div>
        <button @click="refreshMetadata">同步 Schema</button>
      </div>
      <p v-if="metadataMessage" class="success-message">{{ metadataMessage }}</p>
      <div class="metadata-grid">
        <div>
          <h3>表元数据</h3>
          <div v-for="table in tables" :key="table.id" class="metadata-card">
            <strong>{{ table.tableName }}</strong>
            <input v-model="table.bizName" placeholder="业务名称" />
            <textarea v-model="table.bizDescription" placeholder="业务描述 / 表注释" />
            <button @click="saveTable(table)">保存表信息</button>
          </div>
        </div>
        <div>
          <h3>字段元数据</h3>
          <select v-model="selectedTableId" @change="refreshColumns">
            <option v-for="table in tables" :key="table.id" :value="table.id">{{ table.tableName }}</option>
          </select>
          <div v-for="column in columns" :key="column.id" class="metadata-card compact">
            <strong>{{ column.columnName }} · {{ column.columnType }}</strong>
            <input v-model="column.bizName" placeholder="字段业务名" />
            <input v-model="column.columnComment" placeholder="字段注释" />
            <input v-model="column.exampleValue" placeholder="示例值" />
            <button @click="saveColumn(column)">保存字段</button>
          </div>
        </div>
        <div>
          <h3>表关联关系</h3>
          <div class="relation-form">
            <input v-model="relationForm.sourceTable" placeholder="源表" />
            <input v-model="relationForm.sourceColumn" placeholder="源字段" />
            <input v-model="relationForm.targetTable" placeholder="目标表" />
            <input v-model="relationForm.targetColumn" placeholder="目标字段" />
            <input v-model="relationForm.description" placeholder="关系说明" />
            <button @click="addRelation">新增关联</button>
          </div>
          <div v-for="relation in relations" :key="relation.id" class="relation-item">
            <span>{{ relation.sourceTable }}.{{ relation.sourceColumn }} → {{ relation.targetTable }}.{{ relation.targetColumn }}</span>
            <button class="ghost" @click="removeRelation(relation.id)">删除</button>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import {
  createRelation,
  deleteRelation,
  listColumns,
  listDatasources,
  listRelations,
  listTables,
  nl2sql,
  updateColumn,
  updateTable
} from './api/query';
import ChartPanel from './components/ChartPanel.vue';

const question = ref('统计最近30天每天新增用户数，并画成折线图');
const chartType = ref('line');
const datasourceId = ref(1);
const datasources = ref([{ id: 1, name: '示例 MySQL 数据源', bizDomain: 'user' }]);
const loading = ref(false);
const result = ref(null);
const errorMessage = ref('');
const tables = ref([]);
const columns = ref([]);
const relations = ref([]);
const selectedTableId = ref(null);
const metadataMessage = ref('');
const relationForm = ref({
  sourceTable: 'user_info',
  sourceColumn: 'user_id',
  targetTable: 'query_history',
  targetColumn: 'user_id',
  relationType: 'one_to_many',
  description: '用户信息与查询历史按用户 ID 关联'
});

onMounted(async () => {
  try {
    const list = await listDatasources();
    if (Array.isArray(list) && list.length > 0) {
      datasources.value = list;
      datasourceId.value = list[0].id;
    }
  } catch (error) {
    console.warn('加载数据源失败，使用默认示例数据源', error);
  }
  await refreshMetadata();
});

watch(datasourceId, refreshMetadata);

async function submitQuery() {
  loading.value = true;
  errorMessage.value = '';
  try {
    result.value = await nl2sql({
      datasourceId: datasourceId.value,
      bizDomain: 'user',
      question: question.value,
      chartType: chartType.value
    });
  } catch (error) {
    errorMessage.value = error?.response?.data?.message || error.message || '查询失败，请稍后重试';
  } finally {
    loading.value = false;
  }
}

async function refreshMetadata() {
  metadataMessage.value = '';
  tables.value = await listTables(datasourceId.value);
  relations.value = await listRelations(datasourceId.value);
  selectedTableId.value = tables.value[0]?.id || null;
  await refreshColumns();
}

async function refreshColumns() {
  columns.value = selectedTableId.value ? await listColumns(selectedTableId.value) : [];
}

async function saveTable(table) {
  await updateTable(table.id, table);
  metadataMessage.value = '表元数据已保存';
}

async function saveColumn(column) {
  await updateColumn(column.id, column);
  metadataMessage.value = '字段元数据已保存';
}

async function addRelation() {
  await createRelation({ datasourceId: datasourceId.value, ...relationForm.value });
  relations.value = await listRelations(datasourceId.value);
  metadataMessage.value = '表关联关系已新增';
}

async function removeRelation(id) {
  await deleteRelation(id);
  relations.value = await listRelations(datasourceId.value);
  metadataMessage.value = '表关联关系已删除';
}
</script>
