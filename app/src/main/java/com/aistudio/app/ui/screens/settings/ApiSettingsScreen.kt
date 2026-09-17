package com.aistudio.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.aistudio.app.data.remote.AIModelType
import com.aistudio.app.data.remote.ApiConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiSettingsScreen(
    onBack: () -> Unit = {}
) {
    var openAIKey by remember { mutableStateOf(ApiConfig.getOpenAIApiKey()) }
    var claudeKey by remember { mutableStateOf(ApiConfig.getClaudeApiKey()) }
    var selectedModel by remember { mutableStateOf(ApiConfig.getDefaultModel()) }
    var showOpenAIKey by remember { mutableStateOf(false) }
    var showClaudeKey by remember { mutableStateOf(false) }
    var isTesting by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API 设置", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 说明卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "配置 AI API Key 以启用真实的智能对话功能",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            // 模型选择
            Text(
                text = "选择 AI 模型",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModelSelectChip(
                    selected = selectedModel == AIModelType.OPENAI,
                    onClick = { selectedModel = AIModelType.OPENAI },
                    label = "OpenAI",
                    icon = Icons.Default.Psychology,
                    modifier = Modifier.weight(1f)
                )
                ModelSelectChip(
                    selected = selectedModel == AIModelType.CLAUDE,
                    onClick = { selectedModel = AIModelType.CLAUDE },
                    label = "Claude",
                    icon = Icons.Default.Psychology,
                    modifier = Modifier.weight(1f)
                )
            }
            
            HorizontalDivider()
            
            // OpenAI API Key
            Text(
                text = "OpenAI API Key",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = openAIKey,
                onValueChange = { openAIKey = it },
                label = { Text("sk-...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showOpenAIKey) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showOpenAIKey = !showOpenAIKey }) {
                        Icon(
                            if (showOpenAIKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showOpenAIKey) "隐藏" else "显示"
                        )
                    }
                },
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                supportingText = { Text("从 platform.openai.com 获取") }
            )
            
            // Claude API Key
            Text(
                text = "Claude API Key",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = claudeKey,
                onValueChange = { claudeKey = it },
                label = { Text("sk-ant-...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showClaudeKey) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showClaudeKey = !showClaudeKey }) {
                        Icon(
                            if (showClaudeKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showClaudeKey) "隐藏" else "显示"
                        )
                    }
                },
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                supportingText = { Text("从 console.anthropic.com 获取") }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 测试按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        // TODO: 测试 API 连接
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isTesting
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("测试连接")
                }
                
                Button(
                    onClick = {
                        // 保存配置
                        ApiConfig.configureOpenAI(openAIKey)
                        ApiConfig.configureClaude(claudeKey)
                        ApiConfig.setDefaultModel(selectedModel)
                        testResult = "配置已保存"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("保存")
                }
            }
            
            // 测试结果
            testResult?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.contains("成功"))
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (result.contains("成功")) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(result)
                    }
                }
            }
            
            HorizontalDivider()
            
            // 费用说明
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "费用说明",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "• OpenAI GPT-3.5: ~$0.002/1K tokens\n" +
                        "• OpenAI GPT-4: ~$0.03/1K tokens\n" +
                        "• Claude 3 Haiku: ~$0.25/1M tokens\n" +
                        "• Claude 3 Sonnet: ~$3/1M tokens",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModelSelectChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        modifier = modifier
    )
}
