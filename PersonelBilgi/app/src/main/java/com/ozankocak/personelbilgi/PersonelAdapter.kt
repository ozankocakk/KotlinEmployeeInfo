package com.ozankocak.personelbilgi

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ozankocak.personelbilgi.databinding.RecyclerItemBinding

class PersonelAdapter(val personelListe : ArrayList<Personel>) : RecyclerView.Adapter<PersonelAdapter.PersonelHolder>() {

    class PersonelHolder(val binding : RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonelHolder {
        val binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return PersonelHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonelHolder, position: Int) {
        holder.binding.recyclerViewText.text = personelListe.get(position).adsoyad
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,Profil::class.java)
            intent.putExtra("veri","kayitli")
            intent.putExtra("id", personelListe.get(position).id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return personelListe.size
    }
}