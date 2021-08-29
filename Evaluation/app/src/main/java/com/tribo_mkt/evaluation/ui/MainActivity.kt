package com.tribo_mkt.evaluation.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import com.tribo_mkt.evaluation.R
import com.tribo_mkt.evaluation.databinding.ActivityMainBinding
import com.tribo_mkt.evaluation.ui.home.HomeFragment
import com.tribo_mkt.evaluation.utils.ConnectionLiveData
import com.tribo_mkt.evaluation.utils.Util.Companion.ERROR_GET_DATA
import com.tribo_mkt.evaluation.utils.createDialog
import com.tribo_mkt.evaluation.utils.createNetInfoSnackBar
import com.tribo_mkt.evaluation.utils.createProgressDialog

class MainActivity : AppCompatActivity() {

    companion object {
        var hasInternetAvailable = false
    }

    val progressBar by lazy { createProgressDialog() }
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var snackBar: Snackbar? = null
    private var offlineStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        connectivityObserver()//testar a conexão com a internet antes de carregar o fragment.

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        if (!hasInternetAvailable) {//exibir a mensagem sem conexão somente depois que carregar o Fragment.
            connectivityObserverCallback()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun connectivityObserver() {

        val connectionLiveData = ConnectionLiveData(this)
        hasInternetAvailable = connectionLiveData.isInternetAvailable()

        connectionLiveData.observe(this, { isNetworkAvailable ->
            hasInternetAvailable = isNetworkAvailable
            connectivityObserverCallback()
        })
    }

    private fun connectivityObserverCallback() {
        if (!hasInternetAvailable) {
            showMessageWithoutConnection()
            offlineStatus = true
        } else {
            if (offlineStatus) {
                offlineStatus = false
                snackBar?.dismiss()
                refreshHomeFragmentData()
            }
        }
    }

    private fun showMessageWithoutConnection() {
        try {
            snackBar = createNetInfoSnackBar(binding.clayoutMain)
            snackBar?.show()

        } catch (e: Exception) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show()
        }
    }

    private fun refreshHomeFragmentData() {
        try {
            navController.currentDestination?.id.let { fragmentId ->
                if (fragmentId == R.id.nav_home) {
                    val fragment = navHostFragment.childFragmentManager.fragments[0] as HomeFragment
                    fragment.viewModelObserver()
                }
            }
        } catch (e: Exception) {
            setError(ERROR_GET_DATA)
        }
    }

    fun setError(error: String) {
        val msg = getString(R.string.msg_error_get_data)
        val msgError = if (error != ERROR_GET_DATA) "$msg $error" else msg
        createDialog {
            setIcon(R.drawable.ic_warning)
            setTitle(R.string.notice)
            setMessage(msgError)
        }.show()
    }
}