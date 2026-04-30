import axios from 'axios';

const client = axios.create({
  baseURL: '/api',
  timeout: 30000
});

client.interceptors.response.use((response) => {
  const body = response.data;
  if (body?.code !== 0) {
    return Promise.reject(new Error(body?.message || '请求失败'));
  }
  return body.data;
});

export function nl2sql(payload) {
  return client.post('/query/nl2sql', payload);
}

export function listDatasources() {
  return client.get('/datasource/page');
}

export function listTables(datasourceId) {
  return client.get('/metadata/tables', { params: { datasourceId } });
}

export function listColumns(tableId) {
  return client.get(`/metadata/tables/${tableId}/columns`);
}

export function listRelations(datasourceId) {
  return client.get('/metadata/relations', { params: { datasourceId } });
}

export function updateTable(id, payload) {
  return client.put(`/metadata/tables/${id}`, payload);
}

export function updateColumn(id, payload) {
  return client.put(`/metadata/columns/${id}`, payload);
}

export function createRelation(payload) {
  return client.post('/metadata/relations', payload);
}

export function deleteRelation(id) {
  return client.delete(`/metadata/relations/${id}`);
}
