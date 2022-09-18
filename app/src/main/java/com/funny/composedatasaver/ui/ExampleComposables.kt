package com.funny.composedatasaver.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funny.composedatasaver.Constant
import com.funny.composedatasaver.Constant.KEY_BEAN_EXAMPLE
import com.funny.composedatasaver.Constant.KEY_BOOLEAN_EXAMPLE
import com.funny.composedatasaver.Constant.KEY_STRING_EXAMPLE
import com.funny.data_saver.core.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Example usage of this library
 *
 * NOTE: You may see two languages in comments: English and Chinese,
 * they HAVE THE SAME MEANING. you can choose one to read.
 *
 * -----------------------
 * 此仓库的使用示例
 * 注意：下面的注释包含两种语言：中、英文，意思一样的。
 * 选一个读就好
 */

@Serializable
data class ExampleBean(var id:Int, val label:String)
val EmptyBean = ExampleBean(233,"FunnySaltyFish")

@ExperimentalSerializationApi
@Composable
fun ExampleComposable() {
    // get dataSaver                          | 获取 DataSaverInterface
    // you can use this to save data manually | 您可以使用此变量做手动保存
    val dataSaverInterface = LocalDataSaver.current

    // you can set [savePolicy] to other types (see [SavePolicy] ) to prevent saving too frequently
    // if you set it to SavePolicy.NEVER , you need to saveData by yourself
    // eg: onClick = { dataSaverInterface.saveData(key, value) }
    // .............................................................
    // 你可以设置 [savePolicy]为其他类型(参见 [SavePolicy] )，以防止某些情况下过于频繁地保存
    // 如果你设置为 SavePolicy.NEVER，则写入本地的操作需要自己做
    // 例如: onClick = { dataSaverInterface.saveData(key, value) }
    var stringExample by rememberDataSaverState(KEY_STRING_EXAMPLE, "", savePolicy = SavePolicy.IMMEDIATELY, async = true)

    var booleanExample by rememberDataSaverState(KEY_BOOLEAN_EXAMPLE, false)

    var beanExample by rememberDataSaverState(KEY_BEAN_EXAMPLE, default = EmptyBean)

    var listExample by rememberDataSaverListState(key = "key_list_example", default = listOf(
        EmptyBean.copy(label = "Name 1"), EmptyBean.copy(label = "Name 2"),EmptyBean.copy(label = "Name 3")
    ))

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)) {
        Heading(text = "Auto Save Examples:")
        Text(text = "This is an example of saving String") // 保存字符串的示例
        OutlinedTextField(value = stringExample, onValueChange = {
            stringExample = it
        })

        Text(text = "This is an example of saving Boolean") // 保存布尔值的示例
        Switch(checked = booleanExample, onCheckedChange = {
            booleanExample = it
        })

        Text(text = "This is an example of saving custom Data Bean") // 保存自定义类型的示例
        Text(text = beanExample.toString())
        Button(onClick = {
            beanExample = beanExample.copy(id = beanExample.id+1)
        }) {
            Text(text = "Add bean's id") // id自加
        }

        Spacer(modifier = Modifier.height(16.dp))
        Heading(text = "Save-When-Disposed Examples:")
        var showDialog by remember {
            mutableStateOf(false)
        }
        if (showDialog) AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Sample") },
            text = {
                var stringExample2 by rememberDataSaverState(
                    key = Constant.KEY_STRING_EXAMPLE_2,
                    initialValue = "this one will be saved only when disposed",
                    savePolicy = SavePolicy.DISPOSED,
                    async = false
                )
                OutlinedTextField(value = stringExample2, onValueChange = {
                    stringExample2 = it
                })
            },
            confirmButton = { TextButton(onClick = { showDialog = false }){ Text(text = "Close") } },
        )
        Button(onClick = { showDialog = true }) {
            Text(text = "Click Me To Open Dialog")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Heading(text = "List Example")
        LazyColumn {
            items(listExample){ item ->
                Text(modifier = Modifier.padding(8.dp), text = item.toString(), fontSize = 16.sp)
            }
            item {
                Row {
                    Button(onClick = { listExample = listExample + EmptyBean.copy(label = "Name ${listExample.size + 1}") }) {
                        Text(text = "Add To List")
                    }
                    Button(onClick = { if (listExample.isNotEmpty()) listExample = listExample.dropLast(1) }) {
                        Text(text = "Remove From List")
                    }
                }
            }
        }
    }
}

/**
 * This function is used to read custom bean
 * Here we use [Json] to load [String] and convert it into bean
 * You can use whatever you like to do this(eg.Gson/Jackson/Fastjson)
 * ------------------------------------------------------------------
 * 此函数用于读取保存的自定义bean
 * 此处使用 [Json] 将字符串反序列化为bean
 * 当然你也可以用其他的库实现反序列化（Gson/Jackson/Fastjson等）
 *
 * @param key String
 * @param default T
 * @return DataSaverMutableState<T>
 */
//@ExperimentalSerializationApi
//@Composable
//inline fun <reified T> rememberDataSaverBeanState(key:String, default:T, savePolicy: SavePolicy = SavePolicy.IMMEDIATELY) : DataSaverMutableState<T> {
//    val dataSaverInterface = LocalDataSaver.current
//    val jsonData = dataSaverInterface.readData(key,"")
//    return if (jsonData == "") remember(dataSaverInterface, key, savePolicy) {
//        DataSaverMutableState(dataSaverInterface, key, default, savePolicy)
//    } else remember(dataSaverInterface, key, savePolicy) {
//        DataSaverMutableState(dataSaverInterface, key, Json.decodeFromString(jsonData), savePolicy)
//    }
//}

@Composable
fun Heading(text: String) {
    Text(text, fontWeight = FontWeight.W600, fontSize = 18.sp)
}