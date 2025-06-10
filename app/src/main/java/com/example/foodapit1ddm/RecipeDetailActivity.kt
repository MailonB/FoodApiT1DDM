package com.example.foodapit1ddm

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.foodapit1ddm.databinding.ActivityRecipeDetailBinding
import com.example.foodapit1ddm.model.RecipeResponse
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding
    private var photoUri: Uri? = null
    private var photoFile: File? = null
    private var currentRecipe: RecipeResponse? = null

    private val firestore = FirebaseFirestore.getInstance("receitas")
    private val storage = FirebaseStorage.getInstance()

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(this, "Permissão da câmera negada. Não é possível tirar fotos.", Toast.LENGTH_SHORT).show()
            Log.w("RecipeDetailActivity", "Permissão da câmera negada pelo usuário.")
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("RecipeDetailActivity", "takePictureLauncher - resultCode: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            photoUri?.let { uri ->
                photoFile?.let { file ->
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    binding.ivRecipePhoto.setImageBitmap(bitmap)
                    Log.d("RecipeDetailActivity", "Foto tirada e exibida: $uri")
                } ?: run {
                    Log.e("RecipeDetailActivity", "photoFile é nulo após RESULT_OK, arquivo pode ter sido perdido.")
                    Toast.makeText(this, "Erro: Arquivo da foto não encontrado.", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Log.e("RecipeDetailActivity", "photoUri é nulo após RESULT_OK.")
                Toast.makeText(this, "Erro: URI da foto não encontrada.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Foto não tirada ou cancelada.", Toast.LENGTH_SHORT).show()
            Log.d("RecipeDetailActivity", "Captura de foto cancelada ou falhou (resultCode: ${result.resultCode}).")
            photoUri = null
            photoFile = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("RecipeDetailActivity", "onCreate - savedInstanceState: $savedInstanceState")

        if (savedInstanceState != null) {
            currentRecipe = savedInstanceState.getParcelable("current_recipe")
            photoUri = savedInstanceState.getParcelable("photo_uri")
            val photoFilePath = savedInstanceState.getString("photo_file_path")
            photoFilePath?.let { path ->
                photoFile = File(path)
                Log.d("RecipeDetailActivity", "photoFile restaurado do caminho: $path")
            }

            photoUri?.let { uri ->
                binding.ivRecipePhoto.setImageURI(uri)
                Log.d("RecipeDetailActivity", "Foto restaurada e exibida da URI salva: $uri")
            }
            Log.d("RecipeDetailActivity", "Estado restaurado: currentRecipe=$currentRecipe, photoUri=$photoUri, photoFile=$photoFile")

        } else {
            currentRecipe = intent.getParcelableExtra("recipe_item")
            Log.d("RecipeDetailActivity", "Criação inicial: currentRecipe=$currentRecipe")
        }

        currentRecipe?.let { recipe ->
            binding.tvRecipeName.text = recipe.recipe_name ?: "Nome não disponível"
            binding.tvRecipeDescription.text = recipe.recipe_description ?: "Descrição não disponível"

            if (photoUri != null) {
                binding.ivRecipePhoto.setImageURI(photoUri)
                Log.d("RecipeDetailActivity", "Exibindo foto tirada: $photoUri")
            } else if (!recipe.recipe_image.isNullOrEmpty()) {
                Glide.with(this)
                    .load(recipe.recipe_image)
                    .placeholder(R.drawable.placeholder_recipe_image)
                    .error(R.drawable.placeholder_recipe_image)
                    .into(binding.ivRecipePhoto)
                Log.d("RecipeDetailActivity", "Carregando imagem remota: ${recipe.recipe_image}")
            } else {
                binding.ivRecipePhoto.setImageResource(R.drawable.placeholder_recipe_image)
                Log.d("RecipeDetailActivity", "Exibindo placeholder para imagem.")
            }

            recipe.recipe_nutrition?.let { nutrition ->
                val nutritionText = StringBuilder("Nutrição:")
                nutrition.calories?.let { c -> nutritionText.append("\nCalorias: $c") }
                nutrition.carbohydrate?.let { carb -> nutritionText.append("\nCarboidratos: $carb") }
                nutrition.protein?.let { p -> nutritionText.append("\nProteínas: $p") }
                nutrition.fat?.let { f -> nutritionText.append("\nGorduras: $f") }

                binding.tvRecipeDescription.append("\n\n" + nutritionText.toString())
            }

            recipe.recipe_ingredients?.ingredient?.let { ingredientsList ->
                if (ingredientsList.isNotEmpty()) {
                    binding.tvRecipeDescription.append("\n\nIngredientes:\n" + ingredientsList.joinToString("\n"))
                }
            }

            recipe.recipe_types?.recipe_type?.let { typesList ->
                if (typesList.isNotEmpty()) {
                    binding.tvRecipeDescription.append("\n\nTipos: " + typesList.joinToString(", "))
                }
            }

        } ?: run {
            binding.tvRecipeName.text = "Erro ao carregar a receita"
            binding.tvRecipeDescription.text = "As informações da receita não foram encontradas."
            binding.ivRecipePhoto.setImageResource(R.drawable.placeholder_recipe_image)
            Log.e("RecipeDetailActivity", "Receita nula ou não pôde ser carregada/restaurada.")
        }

        binding.btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndDispatchTakePictureIntent()
        }

        binding.btnSaveRecipe.setOnClickListener {
            saveRecipeToFirebase()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("current_recipe", currentRecipe)
        outState.putParcelable("photo_uri", photoUri)
        outState.putString("photo_file_path", photoFile?.absolutePath)
        Log.d("RecipeDetailActivity", "onSaveInstanceState - Estado salvo: currentRecipe=$currentRecipe, photoUri=$photoUri, photoFilePath=${photoFile?.absolutePath}")
    }

    private fun checkCameraPermissionAndDispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            dispatchTakePictureIntent()
        } else {
            requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun dispatchTakePictureIntent() {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "JPEG_${timeStamp}_"
            val storageDir = cacheDir

            val tempPhotoFile = File.createTempFile(fileName, ".jpg", storageDir)
            photoFile = tempPhotoFile

            photoUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                photoFile!!
            )

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            takePictureLauncher.launch(takePictureIntent)
            Log.d("RecipeDetailActivity", "dispatchTakePictureIntent - Lançando câmera com photoUri: $photoUri")
        } catch (e: Exception) {
            Log.e("RecipeDetailActivity", "Erro ao preparar intent da câmera: ${e.message}", e)
            Toast.makeText(this, "Não foi possível iniciar a câmera.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveRecipeToFirebase() {
        val recipeToSave = currentRecipe
        val imageUriToUpload = photoUri

        if (recipeToSave == null) {
            Toast.makeText(this, "Não há receita para salvar.", Toast.LENGTH_SHORT).show()
            Log.w("RecipeDetailActivity", "Tentativa de salvar receita nula.")
            return
        }

        Toast.makeText(this, "Salvando receita...", Toast.LENGTH_LONG).show()
        Log.d("RecipeDetailActivity", "Iniciando processo de salvamento para a receita: ${recipeToSave.recipe_name}")

        if (imageUriToUpload != null) {
            val storageRef = storage.reference
            val imageRef = storageRef.child("recipe_images/${UUID.randomUUID()}.jpg")
            Log.d("RecipeDetailActivity", "Fazendo upload da imagem para: ${imageRef.path}")

            imageRef.putFile(imageUriToUpload)
                .addOnSuccessListener { taskSnapshot ->
                    Log.d("RecipeDetailActivity", "Upload da imagem bem-sucedido.")
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        Log.d("RecipeDetailActivity", "URL da imagem obtida: $imageUrl")

                        recipeToSave.recipe_image = imageUrl
                        saveRecipeDataToFirestore(recipeToSave)
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao obter URL da imagem: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("RecipeDetailActivity", "Erro ao obter URL da imagem: ${e.message}", e)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar imagem: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("RecipeDetailActivity", "Erro ao carregar imagem para o Firebase Storage: ${e.message}", e)
                }
        } else {
            Log.d("RecipeDetailActivity", "Nenhuma nova foto para upload. Salvando dados da receita diretamente.")
            saveRecipeDataToFirestore(recipeToSave)
        }
    }

    private fun saveRecipeDataToFirestore(recipe: RecipeResponse) {
        val recipesCollection = firestore.collection("recipes")
        val recipeId = recipe.recipe_id ?: UUID.randomUUID().toString()

        Log.d("RecipeDetailActivity", "Salvando receita no Firestore com ID: $recipeId")
        recipesCollection.document(recipeId.toString())
            .set(recipe)
            .addOnSuccessListener {
                Toast.makeText(this, "Receita salva com sucesso no Firebase!", Toast.LENGTH_LONG).show()
                Log.i("RecipeDetailActivity", "Receita ${recipe.recipe_name} salva com sucesso no Firestore.")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar receita no Firebase: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("RecipeDetailActivity", "Erro ao salvar receita no Firestore: ${e.message}", e)
            }
    }
}